package org.techforumist.ui.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.techforumist.ui.domain.File;
import org.techforumist.ui.kafka.producer.Sender;
import org.techforumist.ui.repository.FileRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Sarath Muraleedharan
 *
 */
@RestController
public class RestUploadController {

	@Autowired
	FileRepository fileRepository;

	private final Logger logger = LoggerFactory.getLogger(RestUploadController.class);
	private static String UPLOADED_FOLDER = "D:/fileUploaded/";

	private static long count = 0;

	@Autowired
	Sender sender;
	@Autowired
	ObjectMapper mapper;

	@RequestMapping(value = "/api/upload", method = RequestMethod.POST)
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadfile) {
		logger.debug("Single file upload!");
		if (uploadfile.isEmpty()) {
			return new ResponseEntity("please select a file!", HttpStatus.OK);
		}
		try {
			saveUploadedFiles(Arrays.asList(uploadfile));
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		File file = new File();
		file.setFilePath(UPLOADED_FOLDER + uploadfile.getOriginalFilename());
		file.setStatus("Pending");
		file.setUploadCompleteTime(new Date());
		file = fileRepository.save(file);
		try {
			sender.send("file_upload_complete", mapper.writeValueAsString(file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity(file, new HttpHeaders(), HttpStatus.OK);
	}

	private void saveUploadedFiles(List<MultipartFile> files) throws IOException {
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

		}

	}
}