package com.jingweihe.oj.judge.codesandbox;

import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeRequest;
import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandBox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}
