package com.itamnesia.qiwihackathon.service.qiwi;

import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.model.Payment;
import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.repository.PaymentRepository;
import com.itamnesia.qiwihackathon.repository.UserRepository;
import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import com.itamnesia.qiwihackathon.service.transaction.TransactionService;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import com.itamnesia.qiwihackathon.transfer.payment.PaymentRequest;
import com.itamnesia.qiwihackathon.transfer.payment.PaymentResponse;
import com.itamnesia.qiwihackathon.transfer.payment.confirmation.PaymentConfirmationRequest;
import com.itamnesia.qiwihackathon.transfer.payment.confirmation.PaymentConfirmationResponse;
import com.itamnesia.qiwihackathon.transfer.payment.token.DeletePaymentTokenRequest;
import com.itamnesia.qiwihackathon.transfer.payment.transaction.Amount;
import com.itamnesia.qiwihackathon.transfer.payment.transaction.Customer;
import com.itamnesia.qiwihackathon.transfer.payment.transaction.PaymentMethod;
import com.itamnesia.qiwihackathon.transfer.payment.transaction.QiwiTransactionRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class QiwiServiceImpl implements QiwiService {
    private final static String URL = "https://api.qiwi.com/partner";
    @Value("${qiwi.auth.header}")
    private String authHeader;
    @Value("${qiwi.auth.siteId}")
    private String siteId;

    private final TransactionService transactionService;
    private final AccessTokenService paymentAccessTokenService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public QiwiServiceImpl(
            TransactionService transactionService,
            @Qualifier("paymentAccessTokenService")
            AccessTokenService paymentAccessTokenService,
            RestTemplate restTemplate,
            UserRepository userRepository,
            PaymentRepository paymentRepository
    ) {
        this.transactionService = transactionService;
        this.paymentAccessTokenService = paymentAccessTokenService;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void createPaymentRequest(long id) {
        var user = transactionService.generatePayment(id);
        var payment = new PaymentRequest(
                user.getRequestId(),
                user.getPhoneNumber(),
                user.getAccountId()
        );
        var header = getHeaders();
        var body = new HttpEntity<>(payment, header);
        try {
            var paymentResponse =
                    restTemplate.postForObject(
                            "%s/payin-tokenization-api/v1/sites/%s/token-requests".formatted(URL, siteId),
                            body,
                            PaymentResponse.class
                    );
            if (paymentResponse == null) {
                transactionService.deletePayment(user);
                throw new AuthException("Can not send payment request");
            }
            var status = paymentResponse.status();
            if (!"WAITING_SMS".equals(status.getValue())) {
                transactionService.deletePayment(user);
                throw new AuthException("Can not send payment request");
            }
        } catch (Exception e) {
            transactionService.deletePayment(user);
            throw new AuthException("Can not send payment request");
        }
    }

    @Override
    public TokenDTO confirmPayment(long id, String code) {
        var user = transactionService.getUserById(id);
        var confirmation = new PaymentConfirmationRequest(
                user.getRequestId(),
                code
        );
        var header = getHeaders();
        var body = new HttpEntity<>(confirmation, header);
        try {
            var paymentResponse =
                    restTemplate.postForObject(
                            "%s/payin-tokenization-api/v1/sites/%s/token-requests/complete".formatted(URL, siteId),
                            body,
                            PaymentConfirmationResponse.class
                    );
            if (paymentResponse == null) {
                transactionService.deletePayment(user);
                throw new AuthException("Can not send payment confirmation request");
            }
            var status = paymentResponse.status();
            if (!"CREATED".equals(status.getValue())) {
                transactionService.deletePayment(user);
                throw new AuthException("Can not send payment confirmation request");
            }
            var paymentUser = transactionService.startPayment(user, paymentResponse.token().value());
            return new TokenDTO(paymentAccessTokenService.createToken(paymentUser));
        } catch (Exception e) {
            transactionService.deletePayment(user);
            throw new AuthException("Can not send payment confirmation request");
        }
    }

    @Override
    public void sendPayment(long shopId, String token, long money) {
        if (!paymentAccessTokenService.isValid(token)) {
            throw new AuthException("Client token is expired");
        }
        var client = userRepository
                .findByPhoneNumber(paymentAccessTokenService.getPhoneNumber(token))
                .orElseThrow(() -> new AuthException("Token is inavalid"));
        var request = new QiwiTransactionRequest(
                new Amount(money),
                new Customer(client.getAccountId()),
                new PaymentMethod(client.getPaymentToken())
        );
        var header = getHeaders();
        var body = new HttpEntity<>(request, header);
        var paymentId = UUID.randomUUID().toString();
        try {
            var payment = restTemplate.exchange(
                    "%s/payin/v1/sites/%s/payments/%s".formatted(URL, siteId, paymentId),
                    HttpMethod.PUT,
                    body,
                    Payment.class
            ).getBody();
            if (payment == null) {
                deletePayment(client);
                throw new AuthException("Can not create transaction");
            }
            payment.setShop(User.builder().id(shopId).build());
            payment.setPurchaser(User.builder().id(client.getId()).build());
            paymentRepository.save(payment);
            deletePayment(client);
        } catch (Exception exception) {
            deletePayment(client);
            throw new AuthException("Can not create transaction");
        }
    }

    @Override
    public void deletePayment(User user) {
        var header = getHeaders();
        var paymentToken = user.getPaymentToken();
        if (paymentToken == null) return;
        var deleteRequest = new DeletePaymentTokenRequest(paymentToken, user.getAccountId());
        var body = new HttpEntity<>(deleteRequest, header);
        try {
            restTemplate.exchange(
                    "%S/payin/v1/sites/%s/tokens".formatted(URL, siteId),
                    HttpMethod.DELETE,
                    body,
                    Void.class
            );
            transactionService.deletePayment(user);
        } catch (Exception exception) {
            throw new AuthException("Can not delete payment token");
        }
    }

    private HttpHeaders getHeaders() {
        var header = new HttpHeaders();
        header.set("Authorization", "Bearer " + authHeader);
        header.set("Host", "api.qiwi.com");
        header.set("Content-Type", "application/json");
        header.set("Accept", "application/json");
        return header;
    }
}
