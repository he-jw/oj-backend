package com.jingweihe.oj.judge.codesandbox;

import com.jingweihe.oj.judge.codesandbox.impl.ExampleCodeSandBox;
import com.jingweihe.oj.judge.codesandbox.impl.RemoteCodeSandBox;
import com.jingweihe.oj.judge.codesandbox.impl.ThirdPartyCodeSandBox;
import org.springframework.stereotype.Component;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
@Component
public class CodeSandBoxFactory {

    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return
     */
    public static CodeSandBox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandBox();
            case "remote":
                return new RemoteCodeSandBox();
            case "thirdParty":
                return new ThirdPartyCodeSandBox();
            default:
                return new ExampleCodeSandBox();
        }
    }
}

