package myblog.blog.base.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/*
    - 캐시 인터페이스 설정
*/
@Configuration
public class CacheConfig {

    /*
        - EhCache 팩터리 빈 등록
    */
    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactoryBean(){
        return new EhCacheManagerFactoryBean();
    }

    /*
        - Ehcache 등록
          - 메모리 <-> 성능 트레이드 오프를 고려할때 1)레이아웃 캐싱과 2)매핑 비용이 비싼 rss정도만 캐시처리
            - 캐시 폐기는 6시간마다
    */
    @Bean
    public EhCacheCacheManager ehCacheCacheManager(){

        /*
            1. 레이아웃에 필요한 단건 dto 캐시
                - key 0 : 레이아웃 카테고리
                - key 1 : 메인화면 인기 아티클
        */
        CacheConfiguration layoutCacheConfiguration = new CacheConfiguration()
                .eternal(false)
                .timeToIdleSeconds(0)
                .timeToLiveSeconds(21600)
                .maxEntriesLocalHeap(0)
                .memoryStoreEvictionPolicy("LRU")
                .name("layoutCaching");

        Cache layoutCache = new net.sf.ehcache.Cache(layoutCacheConfiguration);
        Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(layoutCache);

        // 2. 레이아웃에 필요한 동적 리스트반환 메서드용 캐시
        CacheConfiguration recentArticleCacheConfiguration = new CacheConfiguration()
                .eternal(false)
                .timeToIdleSeconds(0)
                .timeToLiveSeconds(21600)
                .maxEntriesLocalHeap(0)
                .memoryStoreEvictionPolicy("LRU")
                .name("layoutRecentArticleCaching");

        Cache recentArticleCache = new net.sf.ehcache.Cache(recentArticleCacheConfiguration);
        Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(recentArticleCache);

        // 3. 레이아웃에 필요한 리스트 반환 메서드용 캐시, 유지보수를 위해 분리
        CacheConfiguration recentCommentCacheConfiguration = new CacheConfiguration()
                .eternal(false)
                .timeToIdleSeconds(0)
                .timeToLiveSeconds(21600)
                .maxEntriesLocalHeap(0)
                .memoryStoreEvictionPolicy("LRU")
                .name("layoutRecentCommentCaching");

        Cache recentCommentCache = new net.sf.ehcache.Cache(recentCommentCacheConfiguration);
        Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(recentCommentCache);

        /*
            - 4. seo 캐시
                - key 0 : rss
                - key 1 : sitemap
                - key 2 :
        */
        CacheConfiguration rssConfiguration = new CacheConfiguration()
                .eternal(false)
                .timeToIdleSeconds(0)
                .timeToLiveSeconds(21600)
                .maxEntriesLocalHeap(0)
                .memoryStoreEvictionPolicy("LRU")
                .name("seoCaching");

        Cache rssCache = new net.sf.ehcache.Cache(rssConfiguration);
        Objects.requireNonNull(cacheManagerFactoryBean().getObject()).addCache(rssCache);

        return new EhCacheCacheManager(Objects.requireNonNull(cacheManagerFactoryBean().getObject()));

    }

}
