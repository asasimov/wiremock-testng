package ru.qa.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class StubResponseTransformer extends ResponseTransformer {

    @Override
    public Response transform(Request req, Response res, FileSource files, Parameters parameters) {
        int result = 0;
        int left = Integer.parseInt(req.queryParameter("left").firstValue());
        int right = Integer.parseInt(req.queryParameter("right").firstValue());

        if (req.getUrl().contains("plus")){
            result = left + right;
        } else if (req.getUrl().contains("minus")){
            result = left - right;
        }

        return Response.Builder.like(res).but()
                .body(String.valueOf(result))
                .build();
    }

    @Override
    public String getName() {
        return "stub-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}