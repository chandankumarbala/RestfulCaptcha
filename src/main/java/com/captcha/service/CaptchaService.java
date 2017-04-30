package com.captcha.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import net.jodah.expiringmap.ExpiringMap;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.noise.StraightLineNoiseProducer;

@Component
public class CaptchaService {

	private static final int CAPTCHA_WIDTH=200,CAPTCHA_HEIGHT=50;
	private static final long CAPTCHA_EXPIRY_TIME=60;
	
	private static Map<String, String> captchaCodeMap = ExpiringMap.builder().expiration(new Long(CAPTCHA_EXPIRY_TIME), TimeUnit.SECONDS).build();
	
	private SecureRandom random = new SecureRandom();
	public String nextCaptchaId() {
		return new BigInteger(130, random).toString(32);
	}
	
	public String[] generateCaptchaImage(String previousCaptchaId) {
		
		if(previousCaptchaId!=null)
			removeCaptcha(previousCaptchaId);
		
		Captcha captcha = new Captcha.Builder(CAPTCHA_WIDTH, CAPTCHA_HEIGHT).addText()
				.addBackground(new GradiatedBackgroundProducer()).addNoise(new StraightLineNoiseProducer())
				.gimp(new FishEyeGimpyRenderer()).addBorder().build();

		BufferedImage buf = captcha.getImage();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		String captchaPngImage = null;

		try {
			ImageIO.write(buf, "png", bao);
			bao.flush();
			byte[] imageBytes = bao.toByteArray();
			bao.close();
			captchaPngImage = new String(Base64.getEncoder().encode(imageBytes), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String captchaId=this.nextCaptchaId();
		String[] imageParams = {captchaPngImage,captchaId};
		
		addCaptcha(captchaId,captcha.getAnswer());
		return imageParams;
	}
	
	
	public boolean validateCaptcha(String captchaId,String captchaAnswer){
		boolean result=false;
		if(captchaCodeMap.containsKey(captchaId) && captchaCodeMap.get(captchaId).equals(captchaAnswer))
			result= true;
		
		removeCaptcha(captchaId);
		return result;
	}

	private  static void addCaptcha(String captchaId,String captchaAnswer) {
		captchaCodeMap.putIfAbsent(captchaId, captchaAnswer);
		//System.out.println("+++++ Element added to crunchifyMap:" + captchaId+"=>"+ captchaAnswer);
		//printElement();
	}
	
	private static void removeCaptcha(String captchaId){
		if(captchaCodeMap.containsKey(captchaId)){
			captchaCodeMap.remove(captchaId);
		}
		// printElement();
	}
	
	
	private static void printElement() {
		 
		System.out.println("crunchifyMap Size: " + captchaCodeMap.size() + "\n");
		for (Map.Entry<String, String> entry : captchaCodeMap.entrySet())
		{
		    System.out.println(entry.getKey() + "=>" + entry.getValue());
		}
	}
	

	
	

}
