package com.dty.objectif_dty.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.dty.objectif_dty.domain.Response;
import com.dty.objectif_dty.repository.ResponseRepository;
import com.dty.objectif_dty.web.rest.util.HeaderUtil;
import com.dty.objectif_dty.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Response.
 */
@RestController
@RequestMapping("/api")
public class ResponseResource {

    private final Logger log = LoggerFactory.getLogger(ResponseResource.class);
        
    @Inject
    private ResponseRepository responseRepository;
    
    /**
     * POST  /responses : Create a new response.
     *
     * @param response the response to create
     * @return the ResponseEntity with status 201 (Created) and with body the new response, or with status 400 (Bad Request) if the response has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/responses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Response> createResponse(@RequestBody Response response) throws URISyntaxException {
        log.debug("REST request to save Response : {}", response);
        if (response.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("response", "idexists", "A new response cannot already have an ID")).body(null);
        }
        Response result = responseRepository.save(response);
        return ResponseEntity.created(new URI("/api/responses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("response", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /responses : Updates an existing response.
     *
     * @param response the response to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated response,
     * or with status 400 (Bad Request) if the response is not valid,
     * or with status 500 (Internal Server Error) if the response couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/responses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Response> updateResponse(@RequestBody Response response) throws URISyntaxException {
        log.debug("REST request to update Response : {}", response);
        if (response.getId() == null) {
            return createResponse(response);
        }
        Response result = responseRepository.save(response);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("response", response.getId().toString()))
            .body(result);
    }

    /**
     * GET  /responses : get all the responses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of responses in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/responses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Response>> getAllResponses(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Responses");
        Page<Response> page = responseRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/responses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /responses/:id : get the "id" response.
     *
     * @param id the id of the response to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the response, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/responses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Response> getResponse(@PathVariable Long id) {
        log.debug("REST request to get Response : {}", id);
        Response response = responseRepository.findOne(id);
        return Optional.ofNullable(response)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /responses/:id : delete the "id" response.
     *
     * @param id the id of the response to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/responses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteResponse(@PathVariable Long id) {
        log.debug("REST request to delete Response : {}", id);
        responseRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("response", id.toString())).build();
    }

}
