package org.jxnu.stu.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.jxnu.stu.common.*;
import org.jxnu.stu.controller.vo.ProductVo;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.dao.pojo.Product;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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

    @Autowired
    private ValidationImpl validation;

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

        PageInfo pageInfo = productService.search(productName, productId, pageNum, pageSize);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),pageInfo);
    }

    /**
     * 上传图片接口
     * @param file
     * @param request
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,String>> upload(MultipartFile file, HttpServletRequest request) throws BusinessException {
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file, path, request);
        String url = ftpServerHttpPrefix + targetFileName;
        Map<String,String> fileMap = new HashMap<>();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),fileMap);
    }

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductVo> detail(Integer productId,HttpServletRequest request) throws BusinessException {
        if(productId == null){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"请输入产品id");
        }
        ProductVo productVo = productService.detail(productId);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),productVo);
    }

    @RequestMapping(value = "/set_sale_status",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status,HttpServletRequest request) throws BusinessException {
        if(productId == null || status == null){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR);
        }
        productService.setSaleStatus(productId,status);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),null,"更新产品信息成功");
    }


    /**
     * 新增或者更新产品信息，取决于是否传值了id
     * @param product
     * @return
     */
    @RequestMapping(value = "/save",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse save(@Valid Product product,HttpServletRequest request) throws BusinessException {
        ValidationResult validate = validation.validate(product);
        if(validate.isHasError()){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,validate.getErrMsg());
        }
        productService.save(product);
        String msg = product.getId() == null ? "新增产品信息成功" : "更新产品信息成功";
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),msg);
    }


    /**
     * 基于富文本 simditor
     * @param file
     * @param request
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/richtext_img_upload",method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        Map<String,String> map = Maps.newHashMap();
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file, path, request);
        String url = ftpServerHttpPrefix + targetFileName;
        map.put("success","true");
        map.put("msg","上传成功");
        map.put("file_path",url);
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return map;
    }


}
