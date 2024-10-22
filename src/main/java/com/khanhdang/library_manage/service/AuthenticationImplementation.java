package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.User;
import com.khanhdang.library_manage.exception.AuthenticateException;
import com.khanhdang.library_manage.repository.UserRepository;
import com.khanhdang.library_manage.request.authentication.AuthenticationRequest;
import com.khanhdang.library_manage.request.authentication.IntrospectRequest;
import com.khanhdang.library_manage.response.AuthenticationResponse;
import com.khanhdang.library_manage.response.IntrospectResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.util.*;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
public class AuthenticationImplementation implements AuthenticationService{
    @Autowired
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;


    // Phương thức kiểm tra tính hợp lệ của token
    //JOSEException: Xuất hiện nếu có vấn đề với quá trình xác minh hoặc mã hóa JSON Web Token (JWT).
    //ParseException: Xuất hiện nếu việc phân tích cú pháp token không thành công.
    public IntrospectResponse introspectResponse(IntrospectRequest request)
            throws JOSEException, ParseException {
        // Lấy token từ yêu cầu introspect
        var token = request.getToken();

        // Tạo một verifier (bộ xác minh) với khóa bí mật đã được định nghĩa trước
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        // Phân tích token thành đối tượng SignedJWT để có thể làm việc với nó
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Lấy thời gian hết hạn của token từ các claim
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Xác minh chữ ký của token bằng khóa bí mật
        var verified = signedJWT.verify(verifier);

        // Trả về đối tượng IntrospectResponse, xác định token có hợp lệ hay không
        // Token hợp lệ khi chữ ký đã được xác minh và chưa hết hạn
        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date())) // Kiểm tra token còn hạn không
                .build();
    }

    // Phương thức xác thực người dùng
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Tìm người dùng theo username từ yêu cầu đăng nhập
        // Nếu không tìm thấy, ném ra ngoại lệ AuthenticateException
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticateException("User not existed :" + request.getUsername()));

        // Mã hóa mật khẩu sử dụng BCrypt với độ khó 10
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // So sánh mật khẩu từ yêu cầu với mật khẩu đã lưu trong cơ sở dữ liệu
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        // Nếu mật khẩu không khớp, ném ra ngoại lệ AuthenticateException
        if (!authenticated)
            throw new AuthenticateException("User not existed :" + request.getUsername());

        // Nếu xác thực thành công, tạo một token JWT mới cho người dùng
        var token = generateToken(user);

        // Trả về đối tượng AuthenticationResponse, bao gồm token và trạng thái đã xác thực
        return AuthenticationResponse.builder()
                .token(token) // Token được tạo cho người dùng
                .authenticated(true) // Trạng thái xác thực thành công
                .build();
    }

    // Phương thức tạo token JWT
    private String generateToken(User user){
        // Tạo header cho token, sử dụng thuật toán ký HS512
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        // Tạo các claim cho token, bao gồm username, thời gian phát hành, thời gian hết hạn, và một custom claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // Đối tượng của token (người dùng)
                .issuer("khanhdang.com") // Người phát hành token
                .issueTime(new Date()) // Thời gian phát hành token
                .expirationTime(new Date( // Thời gian hết hạn token (sau 1 giờ)
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope", buildScope(user)) // Claim tùy chỉnh thêm vào token
                .build();

        // Tạo payload từ các claim
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // Tạo đối tượng JWSObject với header và payload
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            // Ký token bằng khóa bí mật
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            // Trả về token dưới dạng chuỗi
            return jwsObject.serialize();
        } catch (JOSEException e) {
            // Ghi log lỗi nếu không thể ký token
            log.error("Cannot generate token : ", e);

            // Ném ra ngoại lệ runtime nếu có lỗi xảy ra trong quá trình ký
            throw new RuntimeException(e);
        }
    }
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> stringJoiner.add(role.getRoleName()));
        }
        return stringJoiner.toString();
    }

}
