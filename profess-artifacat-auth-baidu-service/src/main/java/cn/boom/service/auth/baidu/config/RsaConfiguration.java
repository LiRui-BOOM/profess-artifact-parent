package cn.boom.service.auth.baidu.config;

import cn.boom.framework.common.utils.RsaUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "rsa.key")
public class RsaConfiguration {

    private String PubKeyFile;
    private String PriKeyFile;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void initKey() throws Exception {

        privateKey = RsaUtils.getPrivateKey(PriKeyFile);
        publicKey = RsaUtils.getPublicKey(PubKeyFile);
    }


    public String getPubKeyFile() {
        return PubKeyFile;
    }

    public void setPubKeyFile(String pubKeyFile) {
        PubKeyFile = pubKeyFile;
    }

    public String getPriKeyFile() {
        return PriKeyFile;
    }

    public void setPriKeyFile(String priKeyFile) {
        PriKeyFile = priKeyFile;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
