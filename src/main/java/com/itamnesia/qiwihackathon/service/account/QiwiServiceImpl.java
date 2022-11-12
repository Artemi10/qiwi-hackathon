package com.itamnesia.qiwihackathon.service.account;


import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import com.itamnesia.qiwihackathon.service.user.UserService;
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

    private final UserService userService;
    private final AccessTokenService accessTokenService;
    private final RestTemplate restTemplate;

    @Override
    public void createPaymentRequest(long id) {
        var user = userService.generatePayment(id);
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
                userService.deletePayment(user);
                throw new AuthException("Can not send payment request");
            }
            var status = paymentResponse.status();
            if (!"WAITING_SMS".equals(status.value())) {
                userService.deletePayment(user);
                throw new AuthException("Can not send payment request");
            }
        } catch (Exception e) {
            userService.deletePayment(user);
            throw new AuthException("Can not send payment request");
        }
    }

    @Override
    public String confirmPayment(long id, String code) {
        var user = userService.getUserById(id);
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
                userService.deletePayment(user);
                throw new AuthException("Can not send payment confirmation request");
            }
            var status = paymentResponse.status();
            if (!"CREATED".equals(status.value())) {
                userService.deletePayment(user);
                throw new AuthException("Can not send payment confirmation request");
            }
            var paymentUser = userService.startPayment(user, paymentResponse.token().value());
            return accessTokenService.createAccessPaymentToken(paymentUser);
        } catch (Exception e) {
            userService.deletePayment(user);
            throw new AuthException("Can not send payment confirmation request");
        }
    }

    @Override
    public void sendPayment(long id, String code) {

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
