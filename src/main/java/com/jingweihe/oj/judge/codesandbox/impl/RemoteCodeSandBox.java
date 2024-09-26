package com.jingweihe.oj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jingweihe.oj.common.ErrorCode;
import com.jingweihe.oj.exception.BusinessException;
import com.jingweihe.oj.judge.codesandbox.CodeSandBox;
import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeRequest;
import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeResponse;

/**
 * 远程代码沙箱(实际调用接口)
 */
public class RemoteCodeSandBox implements CodeSandBox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://localhost:8090/executeCode";
        String jsonStr = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(jsonStr)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR.getCode(), ErrorCode.API_REQUEST_ERROR.getMessage());
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
