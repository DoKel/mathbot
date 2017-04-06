package vkinterface.vkapi;

import vkinterface.vkapi.PermissionBuilder.Permission;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class AuthorizerGUI extends Application{
	private static final String TOKEN_HOST = "https://oauth.vk.com/authorize";
	private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
	private static ArrayList<Param> mandatoryTokenParams = new ArrayList<Param>();
	private static String requestUrl;
	private static String tokenUrlCache;
	
	static{
		mandatoryTokenParams.add(
				new Param("v", "5.9"));
		mandatoryTokenParams.add(
				new Param("redirect_uri", REDIRECT_URL));
		mandatoryTokenParams.add(
				new Param("display", "page"));
		mandatoryTokenParams.add(
				new Param("response_type", "token"));
	}
	
	/**
	 * 
	 * @param appId
	 * @param permissions
	 * @return redirection URL from browser
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String askToken(String appId, List<Permission> permissions) throws IOException, URISyntaxException{
		ArrayList<Param> parameters = new ArrayList<Param>();
		
		parameters.add(new Param("client_id", appId));
		parameters.add(new Param("scope", new PermissionBuilder(permissions).toString()));
		parameters.addAll(mandatoryTokenParams);
		
		requestUrl = new RequestBuilder(TOKEN_HOST, parameters).toString();
		
		launch();
		return tokenUrlCache;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		final WebView view = new WebView();
        final WebEngine engine = view.getEngine();
        engine.load(requestUrl);
		
        
        primaryStage.setScene(new Scene(view));
        primaryStage.show();
        
        engine.locationProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.startsWith(REDIRECT_URL)){
					tokenUrlCache=newValue;
					primaryStage.close();
				}
			}
        	
        });
        
	}
}
