$(function(){
	 
	 function getcaptcha(){
		 $.get("http://localhost:8080/api/captchaImg",function(data){
			 var jsonData=JSON.parse(data);
			 $("#captchaImg").attr("src","data:image/png;base64,"+jsonData.captchaImg);
			 $("#captchaImg").attr("captchaId",jsonData.captchaId);
		 });
	 }
	 
	 function reloadCaptcha(){
		 $.get("http://localhost:8080/api/reloadCaptchaImg/"+$("#captchaImg").attr("captchaId"),function(data){
			 var jsonData=JSON.parse(data);
			 $("#captchaImg").attr("src","data:image/png;base64,"+jsonData.captchaImg);
			 $("#captchaImg").attr("captchaId",jsonData.captchaId);
			 $("#captchaAnswer").val("");
		 });
	 }
	 
	 function validateCaptcha(){
		 $.post("http://localhost:8080/api/validateCaptcha/"+$("#captchaImg").attr("captchaId"),{captchaAnswer: $("#captchaAnswer").val()},function(data){
			 var jsonData=JSON.parse(data);
			 if(jsonData.challange==="fail"){
				 alert("The text you have enterd does not match.Please try again.");
				 reloadCaptcha();
			 }else{
				 alert("Captcha validation successful.");
			 }
				 
		 });
	 }
	
	 
	 $("#captchaReload").click(function(){
		 reloadCaptcha();
	 });
	 
	 $("#captchaSubmit").click(function(){
		 validateCaptcha();
	 });
	 
	 $('#captchaAnswer').keypress(function(e) {
	        if (e.keyCode == 13) {
	        	validateCaptcha();
	            return false; // prevent the button click from happening
	        }
	});
	 
	 getcaptcha();
	 
 });
