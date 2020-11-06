package cn.boom.service.user.config;

import cn.boom.framework.common.utils.RsaUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "rsa.key")
public class RsaConfiguration {

    private String PubKeyFile;

    private PublicKey publicKey;

    @PostConstruct
    public void initKey() throws Exception {
        publicKey = RsaUtils.getPublicKey(PubKeyFile);
    }

    public String getPubKeyFile() {
        return PubKeyFile;
    }

    public void setPubKeyFile(String pubKeyFile) {
        PubKeyFile = pubKeyFile;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

}
