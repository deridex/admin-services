package cc.newmercy.contentservices.config;

import cc.newmercy.contentservices.ServerStopper;
import cc.newmercy.contentservices.aws.AssetStorage;
import cc.newmercy.contentservices.aws.S3AssetStorage;
import cc.newmercy.contentservices.config.jackson.ContentServicesModule;
import cc.newmercy.contentservices.jaxrs.ClientFactory;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Neo4jTransactionManager;
import cc.newmercy.contentservices.web.api.v1.asset.AssetRepository;
import cc.newmercy.contentservices.web.api.v1.asset.Neo4jAssetRepository;
import cc.newmercy.contentservices.web.api.v1.sermonseries.Neo4jSermonSeriesRepository;
import cc.newmercy.contentservices.web.api.v1.sermonseries.SermonSeriesRepository;
import cc.newmercy.contentservices.web.id.IdService;
import cc.newmercy.contentservices.web.id.Neo4jIdService;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class ContentServicesConfiguration {
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ObjectMapper jsonMapper() {
        ObjectMapper jsonMapper = new ObjectMapper();

        jsonMapper.registerModule(new JSR310Module());
        jsonMapper.registerModule(new ContentServicesModule());

        return jsonMapper;
    }

    @Bean
    public ServerStopper serverStopper() {
        return new ServerStopper();
    }

    @Bean
    public Client jaxRsClient() {
        return ClientFactory.newClient(jsonMapper());
    }

    @Bean
    public WebTarget neo4j() {
        return jaxRsClient().target("http://127.0.0.1:7474/db/data/transaction");
    }

    @Bean
    public Neo4jTransactionManager neo4jTransactionManager() {
        return new Neo4jTransactionManager(jaxRsClient());
    }

    @Bean
    public Neo4jTransaction neo4jTransaction() {
        return neo4jTransactionManager().getTransaction();
    }

    @Bean
    public SermonSeriesRepository sermonSeriesRepository() {
        return new Neo4jSermonSeriesRepository(idService(), neo4j(), neo4jTransaction());
    }

    @Bean
    public IdService idService() {
        return new Neo4jIdService(neo4j());
    }

    @Bean
    public AmazonS3 s3() {
        AWSCredentials credentials = new BasicAWSCredentials("AKIAJUPSRMLEWZGWZNFQ", "VZwq3vgRAkHWhH5RpUTdsalFOjZVzuhCnGsziKon");

        return new AmazonS3Client(credentials);
    }

    @Bean
    public AssetStorage assetStorage() {
        return new S3AssetStorage("content.newmercy.cc", s3());
    }

    @Bean
    public AssetRepository assetRepository() {
        return new Neo4jAssetRepository(idService(), neo4j(), neo4jTransaction());
    }
}
