package science.mengxin.spring.web.tutorial.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import science.mengxin.spring.web.tutorial.domain.LoginMessage;
import science.mengxin.spring.web.tutorial.domain.LoginMessageType;

import java.util.concurrent.CompletableFuture;


@Service
public class LoginService {

    private static LFUCache<Object, Object> cache = CacheUtil.newLFUCache(200);

    public boolean initialLogin(LoginMessage loginMessage) {
        if (loginMessage.getLoginMessageType() == LoginMessageType.INITIAL
                && !StringUtils.isEmpty(loginMessage.getLoginName())) {
            cache.put(loginMessage.getLoginName(), LoginMessageType.INITIAL);
            return true;
        } else {
            return false;
        }
    }

    public CompletableFuture<String> pollingLogin(LoginMessage loginMessage) {
        CompletableFuture<String> future = new CompletableFuture<>();
        if (loginMessage.getLoginMessageType() == LoginMessageType.POLLING
                && !StringUtils.isEmpty(loginMessage.getLoginName())) {
            LoginMessageType currentStatus = (LoginMessageType) cache.get(loginMessage.getLoginName());
            if (currentStatus != LoginMessageType.INITIAL) {
                future.complete("Please initial the login firstly");
            }
            cache.put(loginMessage.getLoginName(), future);
        } else {
            future.complete("Please check parameters");
        }
        return future;
    }

    public Boolean finishLogin(LoginMessage loginMessage) {
        if (loginMessage.getLoginMessageType() == LoginMessageType.SCANNED
                && !StringUtils.isEmpty(loginMessage.getLoginName())) {
            CompletableFuture<String> future = (CompletableFuture<String>) cache.get(loginMessage.getLoginName());
            if (future == null || loginMessage.getToken() == null) {
                return false;
            }else {
                future.complete("Finished Scanned");
                return true;
            }

        } else {
            return false;
        }
    }
}
