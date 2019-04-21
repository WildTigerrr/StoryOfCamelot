package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.FileLinkDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.service.template.FileLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileLinkServiceImpl implements FileLinkService {

    @Autowired
    private FileLinkDao fileLinkDao;

    @Override
    public FileLink create(FileLink fileLink) {
        // TODO if id == null
        Optional object = fileLinkDao.findById(fileLink.getId());
        FileLink existingFileLink;
        if (object.isPresent()) {
            existingFileLink = (FileLink) object.get();
        } else {
            existingFileLink = fileLinkDao.save(fileLink);
        }
        return existingFileLink;
    }

    @Override
    public void delete(int id) {
        fileLinkDao.findById(id).ifPresent(fileLink -> fileLinkDao.delete(fileLink));
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
