package ru.qa.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class StubResponseTransformer extends ResponseTransformer {

    @Override
    public Response transform(Request req, Response res, FileSource files, Parameters parameters) {
        if (req.queryParameter("operation").isPresent() && req.getUrl().contains("plus")) {
            int left = Integer.parseInt(req.queryParameter("left").firstValue());
            int right = Integer.parseInt(req.queryParameter("right").firstValue());
            return Response.Builder.like(res).but()
                    .body(String.valueOf(left + right))
                    .build();
        }
        return res;
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