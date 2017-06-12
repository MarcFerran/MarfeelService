package com.marfeel.multithreading;

import com.marfeel.model.Site;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * Created by masber on 11/06/2017.
 */
@Component
@Scope("prototype")
public class SiteChecker implements Callable<Site> {

    private Site site;

    public void setSite(Site site){
        this.site = site;
    }

    @Override
    public Site call() throws Exception {

        String url = site.getUrl().startsWith("http://") ? site.getUrl() : String.format("http://www.%s", site.getUrl());

        try {
            Document doc = Jsoup.connect(url).get();
            if (doc.title() != null && (doc.title().contains("news") || doc.title().contains("noticias"))) {
                site.setMarfeelizable(true);
            }
        } catch (Exception e) {
            site.setError(e.toString());
        }

        return site;
    }
}
