package org.jxnu.stu.controller.backend;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageInfo;
import org.jxnu.stu.common.BusinessException;
import org.jxnu.stu.common.Constant;
import org.jxnu.stu.common.ReturnCode;
import org.jxnu.stu.common.ServerResponse;
import org.jxnu.stu.controller.vo.ProductVo;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.dao.pojo.Product;
import org.jxnu.stu.dao.pojo.User;
import org.jxnu.stu.service.FileService;
import org.jxnu.stu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Value("${ftp.server.http.prefix}")
    private String ftpServerHttpPrefix;

    /**
     * 列出所有商品
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, HttpSession session) throws BusinessException {
        UserVo user = (UserVo) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        PageInfo pageInfo = productService.list(pageNum, pageSize);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),pageInfo);
    }

    /**
     * 根据商品Id或者商品名称查询
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public  ServerResponse<PageInfo> search(String productName,Integer productId,@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize, HttpSession session) throws BusinessException {

        UserVo user = (UserVo) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        PageInfo pageInfo = productService.search(productName, productId, pageNum, pageSize);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),pageInfo);
    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> upload(MultipartFile file, HttpServletRequest request) throws BusinessException {
        UserVo user = (UserVo) request.getSession().getAttribute(Constant.CURRENT_USER);
        if(user == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file, path, request.getSession());
        String url = ftpServerHttpPrefix + targetFileName;
        Map<String,String> fileMap = new HashMap<>();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),fileMap);
    }


}
