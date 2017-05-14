package org.techforumist.ui.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.techforumist.ui.domain.File;
import org.techforumist.ui.repository.FileRepository;
import org.techforumist.ui.websocket.WebSocketMessageHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Sarath Muraleedharan
 *
 */
@RestController
public class FileProcessingListenerController {

	@Autowired
	ObjectMapper mapper;
	@Autowired
	WebSocketMessageHandler sebSocketMessageHandler;

	@Autowired
	FileRepository fileRepository;

	@RequestMapping(value = "/api/fileProcessingComplete", method = RequestMethod.GET)
	public ResponseEntity<?> fileProcessingComplete(@RequestParam("fileId") Long fileId)
			throws JsonProcessingException {

		System.out.println("Completed: >>>>>>" + fileId + "<<<<<<");

		File file = fileRepository.findOne(fileId);
		file.setStatus("Completed");
		file.setEndTime(new Date());
		file = fileRepository.save(file);
		sebSocketMessageHandler.sendMessage(mapper.writeValueAsString(file));
		return new ResponseEntity(file, new HttpHeaders(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/fileProcessingStarted", method = RequestMethod.GET)
	public ResponseEntity<?> fileProcessingStarted(@RequestParam("fileId") Long fileId) throws JsonProcessingException {

		System.out.println("Started : >>>>>>" + fileId + "<<<<<<");

		File file = fileRepository.findOne(fileId);
		file.setStartTime(new Date());
		file.setStatus("Started");
		file = fileRepository.save(file);
		sebSocketMessageHandler.sendMessage(mapper.writeValueAsString(file));

		return new ResponseEntity(file, new HttpHeaders(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/files", method = RequestMethod.GET)
	public ResponseEntity<?> getFiles() throws JsonProcessingException {
		List<File> files = fileRepository.findAll();
		System.out.println(files);
		return new ResponseEntity(files, new HttpHeaders(), HttpStatus.OK);
	}
}