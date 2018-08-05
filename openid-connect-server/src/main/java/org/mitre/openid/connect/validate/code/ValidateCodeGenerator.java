package org.mitre.openid.connect.validate.code;

import org.mitre.openid.connect.model.ValidateCode;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 验证码生成接口
 * ClassName: ValidateCodeGenerator
 *
 * @Description: 所有验证码具体实现的接口
 */
public interface ValidateCodeGenerator {

	ValidateCode generator();

}
