package com.agrismart.agrimallbackend.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * JWT (JSON Web Token) 工具类。
 *
 * 提供 JWT token 的生成、验证和解析功能。
 * 使用 HMAC256 算法进行签名，token 有效期为 7 天。
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 生成 token
 * Map<String, String> claims = new HashMap<>();
 * claims.put("id", "1");
 * claims.put("username", "test");
 * String token = JwtUtil.generateToken(claims);
 *
 * // 验证和解析 token
 * DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
 * String username = decodedJWT.getClaim("username").asString();
 * }
 * </pre>
 *
 * @author agrimall
 * @since 1.0
 */
public final class JwtUtil {

    /**
     * JWT 签名密钥。
     *
     * 注意：生产环境建议将此密钥配置到配置文件中，而不是硬编码。
     *
     */
    private static final String SIGN = "xuanqixian666";

    /**
     * 私有构造函数，防止实例化。
     * 这是一个工具类，所有方法都是静态方法，不需要实例化。
     */
    private JwtUtil() {
    }

    /**
     * 生成 JWT token。
     *
     * 该方法是 {@link #getToken(Map)} 的别名，提供更语义化的方法名。
     *
     * @param claims 要包含在 token 中的声明（claims），键值对形式
     * @return 生成的 JWT token 字符串
     * @see #getToken(Map)
     */
    public static String generateToken(Map<String, String> claims) {
        return getToken(claims);
    }

    /**
     * 生成 JWT token。
     *
     * 根据提供的声明（claims）生成 JWT token，token 有效期为 7 天。
     * 使用 HMAC256 算法进行签名。
     *
     * @param claims 要包含在 token 中的声明（claims），键值对形式
     * @return 生成的 JWT token 字符串
     */
    public static String getToken(Map<String, String> claims) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 7);
        JWTCreator.Builder builder = JWT.create().withExpiresAt(instance.getTime());
        claims.forEach(builder::withClaim);
        return builder.sign(Algorithm.HMAC256(SIGN));
    }

    /**
     * 解码 JWT token。
     *
     * 该方法是 {@link #verifyToken(String)} 的别名，提供更语义化的方法名。
     * 注意：该方法会验证 token 的签名和有效期。
     *
     * @param token 待解码的 JWT token 字符串
     * @return 解码后的 DecodedJWT 对象，包含所有声明信息
     * @throws com.auth0.jwt.exceptions.JWTVerificationException 如果 token 无效、过期或签名不匹配
     * @see #verifyToken(String)
     */
    public static DecodedJWT decode(String token) {
        return verifyToken(token);
    }

    /**
     * 验证并解析 JWT token。
     *
     * 验证 token 的签名是否有效，并检查 token 是否过期。
     * 如果验证通过，返回解码后的 DecodedJWT 对象，可以通过该对象获取 token 中的声明信息。
     *
     * @param token 待验证的 JWT token 字符串
     * @return 解码后的 DecodedJWT 对象，包含所有声明信息
     * @throws com.auth0.jwt.exceptions.JWTVerificationException 如果 token 无效、过期或签名不匹配
     */
    public static DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }
}
