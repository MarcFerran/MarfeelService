package com.marfeel.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.marfeel.model.Site;
import com.marfeel.multithreading.SiteChecker;
import lombok.Lombok;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@RestController
public class MarfeelRestController {

	@Autowired
	private MongoOperations mongoOperation;

	@Value("${marfeel.threadNumber}" )
	private Integer threadNumber;

    @Autowired
    private ApplicationContext appContext;

	@RequestMapping(value = "/marfeel/", method = RequestMethod.POST)
	public ResponseEntity<List<Site>> marfeelizableRequest(@RequestBody List<Site> sites) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

        List<Future<Site>> resultList = new ArrayList<>();

		for(Site site: sites) {

			SiteChecker siteChecker = (SiteChecker) appContext.getBean("siteChecker");
			siteChecker.setSite(site);

            resultList.add(executorService.submit(siteChecker));
		}
        List<Site> resultSites = resultList.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw Lombok.sneakyThrow(e);
            }
        }).collect(toList());

        //shut down the executor service now
        executorService.shutdown();

        List<String> urlsToUpdate = resultSites.stream().map(site -> site.getUrl()).collect(toList());

        mongoOperation.remove(Query.query(Criteria.where("url").in(urlsToUpdate)), Site.class);
        mongoOperation.insert(resultSites, Site.class);

        return new ResponseEntity<>(resultSites, HttpStatus.CREATED);
	}

    @RequestMapping(value = "/marfeel/{url}/", method = RequestMethod.GET)
    public ResponseEntity<List<Site>> marfeelizableResponse(@PathVariable("url") String url) {
        List<Site> result = mongoOperation.find(Query.query(Criteria.where("url").is(url)), Site.class);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
