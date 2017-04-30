package com.captcha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.util.Methods;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

@RestController
@RequestMapping("/api")
public class CaptchaController {

	@Autowired
	CaptchaService service;
	
	@RequestMapping(value="/captchaImg" , method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getCaptchaImage(){
		String[] captchaData=service.generateCaptchaImage(null);
		JsonObject response=new JsonObject();
		response.addProperty("captchaId", captchaData[1]);
		response.addProperty("captchaImg", captchaData[0]);
		return response.toString();
	}
	
	
	@RequestMapping(value="/reloadCaptchaImg/{previousCaptchaId}" , method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String reloadCaptchaImage(@PathVariable("previousCaptchaId") String previousCaptchaId){
		String[] captchaData=service.generateCaptchaImage(previousCaptchaId);
		JsonObject response=new JsonObject();
		response.addProperty("captchaId", captchaData[1]);
		response.addProperty("captchaImg", captchaData[0]);
		return response.toString();
	}
	
	@RequestMapping(value="/validateCaptcha/{captchaId}" , method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String challangeCaptcha(@PathVariable("captchaId") String captchaId,@RequestParam(value="captchaAnswer",required=false) String captchaAnswer){
		boolean challange=false;
		if((captchaAnswer!=null && captchaAnswer.trim().length()!=0) && (captchaId!=null && captchaId.trim().length()!=0))
			challange=service.validateCaptcha(captchaId, captchaAnswer);
		
		JsonObject response=new JsonObject();
		response.addProperty("challange", challange?"success":"fail");
		return response.toString();
	}
}
