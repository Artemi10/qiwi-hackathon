package com.itamnesia.qiwihackathon.service.qiwi;


import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.repository.UserRepository;
import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import com.itamnesia.qiwihackathon.service.payment.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class QiwiServiceImpl implements QiwiService {
    private final static String URL = "https://api.qiwi.com/partner";
    private final static String HEADER = "8a1e01ee-0482-4598-8b97-d4d79f767107";

    private final PaymentService paymentService;
    private final AccessTokenService accessTokenService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

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
    public String confirmPayment(long id, String code) {
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
            return accessTokenService.createAccessPaymentToken(paymentUser);
        } catch (Exception e) {
            paymentService.deletePayment(user);
            throw new AuthException("Can not send payment confirmation request");
        }
    }

    @Override
    public void sendPayment(long shopId, String token) {
        if (!accessTokenService.isValid(token)) {
            throw new AuthException("Client token is expired");
        }
        var clientPhoneNumber = accessTokenService.getPhoneNumber(token);
        var client = userRepository.findByPhoneNumber(clientPhoneNumber);
    }

    private HttpHeaders getHeaders() {
        var header = new HttpHeaders();
        header.set("Authorization", "Bearer " + HEADER);
        header.set("Host", "api.qiwi.com");
        return header;
    }

    private final record PaymentRequest(String requestId, String phone, String accountId) {}

    private final record PaymentResponse(String requestId, StatusResponse status) {}

    private final record StatusResponse(String value) {}

    private final record PaymentConfirmationRequest(String requestId, String smsCode) {}

    private final record PaymentConfirmationResponse(String requestId, StatusResponse status, TokenResponse token) {}

    private final record TokenResponse(String value, String expiredDate) {}
}
