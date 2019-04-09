package org.jxnu.stu.service.impl;

import com.google.common.collect.Lists;
import org.jxnu.stu.common.BusinessException;
import org.jxnu.stu.common.Constant;
import org.jxnu.stu.common.ReturnCode;
import org.jxnu.stu.controller.vo.ProductVo;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.service.FileService;
import org.jxnu.stu.util.DateTimeHelper;
import org.jxnu.stu.util.FTPHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(MultipartFile file, String path, HttpSession session) throws BusinessException{
        String originalFilename = file.getOriginalFilename();
        String extensionName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //上传文件名拼接逻辑：当前时间+用户id
        String now = DateTimeHelper.dateToString(new Date()).replaceAll(" ","").replaceAll("-","").replaceAll(":","");
        UserVo userVo = (UserVo) session.getAttribute(Constant.CURRENT_USER);
        String userId = String.valueOf(userVo.getId());
        String uploadFileName = now + userId + extensionName;
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);//设置写权限
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //此时上传成功，然后需要异步上传到 vsftp
            FTPHelper.uploadFile(Lists.newArrayList(targetFile));
            //上传完成之后要删除该文件夹下的文件
            targetFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ReturnCode.ERROR,"上传文件异常");
        }
        return targetFile.getName();
    }

}
