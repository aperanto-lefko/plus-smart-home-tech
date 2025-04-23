package ru.yandex.practicum.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


@Slf4j
@Component
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Логируем основную информацию о запросе
        log.info("\n=== Incoming Request ===\n" +
                        "Method: {}\n" +
                        "URL: {}\n" +
                        "Headers:\n{}" +
                        "Parameters:\n{}",
                httpRequest.getMethod(),
                httpRequest.getRequestURL(),
                getHeadersAsString(httpRequest),
                getParametersAsString(httpRequest));

        // Для логирования тела запроса (только для POST/PUT)
        if (isRequestBodySupported(httpRequest)) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
            chain.doFilter(wrappedRequest, response);
            logRequestBody(wrappedRequest);
        } else {
            chain.doFilter(request, response);
        }
    }

    private String getHeadersAsString(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName ->
                        headers.append("  ")
                                .append(headerName)
                                .append(": ")
                                .append(request.getHeader(headerName))
                                .append("\n"));
        return headers.toString();
    }

    private String getParametersAsString(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        request.getParameterMap().forEach((name, values) -> {
            params.append("  ")
                    .append(name)
                    .append("=")
                    .append(String.join(",", values));
        });
        return params.toString();
    }

    private boolean isRequestBodySupported(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                || "PUT".equalsIgnoreCase(request.getMethod())
                || "PATCH".equalsIgnoreCase(request.getMethod());
    }

    private void logRequestBody(ContentCachingRequestWrapper request) throws UnsupportedEncodingException {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            log.info("Request Body:\n{}", new String(content, request.getCharacterEncoding()));
        }
    }
}
