package com.jingweihe.oj.judge.codesandbox.impl;

import com.jingweihe.oj.judge.codesandbox.CodeSandBox;
import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeRequest;
import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeResponse;

/**
 * 第三方代码沙箱(实际调用接口)
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
