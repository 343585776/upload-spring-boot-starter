## 通用单文件/多文件上传场景启动器

1. 该启动器支持单文件与多文件的上传操作
2. 支持文件存储本地(默认)
3. 支持拓展使用云存储
4. 支持通过检测真实文件内容从而控制上传文件类型(防止更改后缀名进行伪造)

### 引入starter

```xml
<dependency>
    <groupId>me.sdevil507</groupId>
    <artifactId>upload-spring-boot-starter</artifactId>
    <version>1.2.0</version>
</dependency>
```

### 文件上传地址

**单文件上传:**

`http(s)://ip:port/file/upload/single`

**多文件上传:**

`http(s)://ip:port/file/upload/multiple`

### 注意事项(只针对1.2.0以下版本,1.2.0及以上版本可自动发现!)

因为提供了通用的上传Controller,所以要确保该Controller能够被SpringBoot正确扫描到。

SpringBoot默认扫描方式是从Application启动类所在包向下扫描,如果Application启动类所在层级较深，则需要使用`@ComponentScan`设置扫描路径。

如果设置了`@ComponentScan`后,会替换SpringBoot默认的扫描包路径,所以需要将项目的原包路经添加到注解后边以逗号分隔,类似:

@ComponentScan({"me.sdevil507.upload","xxx.xxx"})

通用Controller所在包为`me.sdevil507.upload`下面,如果需要设置扫描路径,参考如下:

```java
@SpringBootApplication
@ComponentScan({"me.sdevil507.upload"})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 本地存储方案

启动器提供了`AbstractUploadFileHandler`的子类`LocalUploadFileHandler`实现本地保存上传文件

如果使用本地保存文件，则可以直接按照如下设置直接进行使用。

**配置如下:**

```yml
spring:
  resources:
    # 设置上传文件本地服务器地址,可对外提供访问(http(s)://ip:port/xxx.jpg)
    static-locations: file:${file.upload.local-save-path}
    # 如果项目使用了静态资源,则需要将原路径也加上,例如:
    # static-locations: classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/,file:${file.upload.local-save-path}

file:
  upload:
    # 如果设置了该属性,则默认使用本地文件保存方案(绝对路径)
    local-save-path: /Users/sdevil507/upload_files
    # 针对上传文件类型进行控制(不设置默认全部支持)
    include-extensions: png,jpg
```

当前本地存储方案文件名称生成默认使用的是UUID,如果有特殊命名需求则可以继承`AbstractUploadFileHandler`抽象类,自己实现相关需求。

### 云存储方案

云存储方案需要在各自项目中继承`AbstractUploadFileHandler`抽象类,实现save()方法。但是仍然可以使用文件类型真实检测控制。

**配置如下:**

```yml
file:
  upload:
    # 不需要设置本地保存绝对地址
    # local-save-path: /Users/sdevil507/upload_files
    # 针对上传文件类型进行控制(不设置默认全部支持)
    include-extensions: png,jpg
```

**七牛云存储保存demo:**

```java
/**
 * 七牛云存储上传
 *
 * @author sdevil507
 */
@Slf4j
@Component
public class QiNiuUploadFileHandler extends AbstractUploadFileHandler {

    public QiNiuUploadFileHandler(FileDetectionHelper fileDetectionHelper) {
        super(fileDetectionHelper);
    }

    @Override
    protected String save(File tempFile, String originalFileName) {
        try {
            //密钥配置
            Auth auth = Auth.create("xxx", "xxx");
            //自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
            Zone z = Zone.autoZone();
            Configuration c = new Configuration(z);
            //创建上传对象
            UploadManager uploadManager = new UploadManager(c);
            //简单上传，使用默认策略，只需要设置上传的空间名就可以了
            String token = auth.uploadToken("sdevil507");
            //构建上传文件,并上传
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID() + extension;
            Response response = uploadManager.put(FileCopyUtils.copyToByteArray(tempFile), fileName, token);
            //上传反馈
            if (response.isOK()) {
                return fileName;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("七牛云上传失败:", e);
            return null;
        }
    }
}
```

### 单元测试示例

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ComponentScan(value = "me.sdevil507.upload")
public class DemoApplicationTests {
    
    @Autowired
    private MockMvc mvc;

    /**
     * 测试单文件上传
     */
    @Test
    public void singleUpload() {
        try {
            // 获取文件
            File file = ResourceUtils.getFile("classpath:files/test.jpg");
            // 封装inputStream
            FileInputStream fileInputStream = new FileInputStream(file);
            // 构建mock上传文件对象
            MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file.getName(), "multipart/form-data", fileInputStream);
            // 执行上传操作
            MvcResult result = mvc.perform(MockMvcRequestBuilders.fileUpload("/file/upload/single")
                    .file(mockMultipartFile)
            ).andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            // 获取返回结果
            String resultStrings = result.getResponse().getContentAsString();
            System.out.println(resultStrings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试多文件上传
     */
    @Test
    public void multipleUpload() {
        try {
            // 获取文件
            File file1 = ResourceUtils.getFile("classpath:files/test.jpg");
            File file2 = ResourceUtils.getFile("classpath:files/test.png");
            File file3 = ResourceUtils.getFile("classpath:files/test.xlsx");
            // 封装inputStream
            FileInputStream fileInputStream1 = new FileInputStream(file1);
            FileInputStream fileInputStream2 = new FileInputStream(file2);
            FileInputStream fileInputStream3 = new FileInputStream(file3);
            // 构建mock上传文件对象
            MockMultipartFile mockMultipartFile1 = new MockMultipartFile("files", file1.getName(), "multipart/form-data", fileInputStream1);
            MockMultipartFile mockMultipartFile2 = new MockMultipartFile("files", file2.getName(), "multipart/form-data", fileInputStream2);
            MockMultipartFile mockMultipartFile3 = new MockMultipartFile("files", file3.getName(), "multipart/form-data", fileInputStream3);
            // 执行上传操作
            MvcResult result = mvc.perform(MockMvcRequestBuilders.fileUpload("/file/upload/multiple")
                    .file(mockMultipartFile1)
                    .file(mockMultipartFile2)
                    .file(mockMultipartFile3)
            ).andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            // 获取返回结果
            String resultStrings = result.getResponse().getContentAsString();
            System.out.println(resultStrings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
