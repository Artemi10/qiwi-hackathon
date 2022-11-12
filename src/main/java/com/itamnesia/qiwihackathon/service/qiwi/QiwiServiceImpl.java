package com.itamnesia.qiwihackathon.service.qiwi;

import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.model.Payment;
import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.repository.UserRepository;
import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import com.itamnesia.qiwihackathon.service.payment.PaymentService;
import com.itamnesia.qiwihackathon.transfer.auth.TokensDTO;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class QiwiServiceImpl implements QiwiService {
    private final static String URL = "https://api.qiwi.com/partner";
    private final static String HEADER = "8a1e01ee-0482-4598-8b97-d4d79f767107";

    private final PaymentService paymentService;
    private final AccessTokenService paymentAccessTokenService;
    private final AccessTokenService applicationAccessTokenService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    public QiwiServiceImpl(
            PaymentService paymentService,
            @Qualifier("paymentAccessTokenService")
            AccessTokenService paymentAccessTokenService,
            @Qualifier("applicationAccessTokenService")
            AccessTokenService applicationAccessTokenService,
            RestTemplate restTemplate,
            UserRepository userRepository
    ) {
        this.paymentService = paymentService;
        this.paymentAccessTokenService = paymentAccessTokenService;
        this.applicationAccessTokenService = applicationAccessTokenService;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public void createPaymentRequest(long id) {
        var user = paymentService.generatePayment(id);
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
                            URL + "/payin-tokenization-api/v1/sites/sa3khn-15/token-requests",
                            body,
                            PaymentResponse.class
                    );
            if (paymentResponse == null) {
                paymentService.deletePayment(user);
                throw new AuthException("Can not send payment request");
            }
            var status = paymentResponse.status();
            if (!"WAITING_SMS".equals(status.value())) {
                paymentService.deletePayment(user);
                throw new AuthException("Can not send payment request");
            }
        } catch (Exception e) {
            paymentService.deletePayment(user);
            throw new AuthException("Can not send payment request");
        }
    }

    @Override
    public TokensDTO confirmPayment(long id, String code) {
        var user = paymentService.getUserById(id);
        var confirmation = new PaymentConfirmationRequest(
                user.getRequestId(),
                code
        );
        var header = getHeaders();
        var body = new HttpEntity<>(confirmation, header);
        try {
            var paymentResponse =
                    restTemplate.postForObject(
                            URL + "/payin-tokenization-api/v1/sites/sa3khn-15/token-requests/complete",
                            body,
                            PaymentConfirmationResponse.class
                    );
            if (paymentResponse == null) {
                paymentService.deletePayment(user);
                throw new AuthException("Can not send payment confirmation request");
            }
            var status = paymentResponse.status();
            if (!"CREATED".equals(status.value())) {
                paymentService.deletePayment(user);
                throw new AuthException("Can not send payment confirmation request");
            }
            var paymentUser = paymentService.startPayment(user, paymentResponse.token().value());
            return new TokensDTO(
                    applicationAccessTokenService.createToken(paymentUser),
                    paymentAccessTokenService.createToken(paymentUser)
            );
        } catch (Exception e) {
            paymentService.deletePayment(user);
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
            var payment = restTemplate.postForObject(
                    URL + "/payin/v1/sites/sa3khn-15/payments/" + paymentId,
                    body,
                    Payment.class
            );
            System.out.println(payment);
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
                    URL + "/payin/v1/sites/sa3khn-15/tokens",
                    HttpMethod.DELETE,
                    body,
                    Void.class
            );
            paymentService.deletePayment(user);
        } catch (Exception exception) {
            throw new AuthException("Can not delete payment token");
        }
    }

    private HttpHeaders getHeaders() {
        var header = new HttpHeaders();
        header.set("Authorization", "Bearer " + HEADER);
        header.set("Host", "api.qiwi.com");
        header.set("Content-Type", "application/json");
        header.set("Accept", "application/json");
        return header;
    }
}
