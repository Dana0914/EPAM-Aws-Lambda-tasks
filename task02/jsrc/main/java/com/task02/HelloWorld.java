package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;


import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
	private static final int SC_OK = 200;
	private static final int SC_BAD_REQUEST = 400;
	private final Map<String, String> responseHeaders = Map.of("Content-Type", "application/json");
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


	@Override
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
		String method = event.getRequestContext().getHttp().getMethod();
		String path = event.getRawPath();


		if ("GET".equals(method) && "/hello".equals(path)) {
			return buildResponse(SC_OK, Body.ok("Hello from Lambda"));
		} else {
			return buildResponse(SC_BAD_REQUEST, Body.error("Bad request syntax or unsupported method. " +
					"Request path: " + path + ". HTTP method: " + method));

		}
	}

	private APIGatewayV2HTTPResponse buildResponse(int statusCode, Object body) {
		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(statusCode)
				.withHeaders(responseHeaders)
				.withBody(gson.toJson(body))
				.build();
	}
	private record Body(String message, String error) {
		static Body ok(String message) {
			return new Body(message, null);
		}

		static Body error(String error) {
			return new Body(null, error);
		}
	}

}
