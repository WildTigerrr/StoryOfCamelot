package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.FileLinkDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.FileLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileLinkServiceImpl implements FileLinkService {

    private final FileLinkDao fileLinkDao;

    @Autowired
    public FileLinkServiceImpl(FileLinkDao fileLinkDao) {
        this.fileLinkDao = fileLinkDao;
    }

    @Override
    public FileLink create(FileLink newLink) {
        FileLink fileLink = null;
        if (newLink.getId() != null) {
            Optional<FileLink> object = fileLinkDao.findById(newLink.getId());
            if (object.isPresent()) {
                fileLink = object.get();
            }
        } else {
            fileLink = fileLinkDao.save(newLink);
        }
        return fileLink;
    }

    @Override
    public void create(ArrayList<FileLink> fileLinks) {
        fileLinkDao.saveAll(fileLinks);
    }

    @Override
    public void delete(String id) {
        fileLinkDao.findById(id).ifPresent(fileLinkDao::delete);
    }

    @Override
    public FileLink update(FileLink fileLink) {
        return fileLinkDao.save(fileLink);
    }

    @Override
    public List<FileLink> getAll() {
        return (List<FileLink>) fileLinkDao.findAll();
    }
}
