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
    <version>1.0.0</version>
</dependency>
```

### 文件上传地址

单文件上传:

`http(s)://ip:port/file/upload/single`

多文件上传:

`http(s)://ip:port/file/upload/multiple`

### 注意事项

因为提供了通用的上传Controller,所以要确保该Controller能够被SpringBoot正确扫描到。

SpringBoot默认扫描方式是从Application启动类所在包向下扫描,如果Application启动类所在层级较深，则需要使用`@ComponentScan`设置扫描路径。

通用Controller所在包为`me.sdevil507.upload`下面,如果需要设置扫描路径,参考如下:

```java
@SpringBootApplication
@ComponentScan(value = "me.sdevil507.upload")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 本地存储方案

启动器提供了`AbstractUploadFileHandler`的子类`LocalUploadFileHandler`实现本地保存上传文件

如果使用本地保存文件，则可以直接按照如下设置直接进行使用：

```yml
spring:
  resources:
    # 设置上传文件本地服务器地址,可对外提供访问(http(s)://ip:port/xxx.jpg)
    static-locations: file:${file.upload.local-save-path}

file:
  upload:
    # 如果设置了该属性,则默认使用本地文件保存方案
    local-save-path: /Users/sdevil507/Downloads/upload
    # 针对上传文件类型进行控制(不设置默认全部支持)
    include-extensions: png,jpg
```

当前本地存储方案文件名称生成默认使用的是UUID,如果有特殊命名需求则可以继承`AbstractUploadFileHandler`抽象类,自己实现相关需求。

### 云存储方案

云存储方案需要在各自项目中继承`AbstractUploadFileHandler`抽象类,实现save()方法。但是仍然可以使用文件类型真实检测控制。

配置如下:

```yml
file:
  upload:
    # 针对上传文件类型进行控制(不设置默认全部支持)
    include-extensions: png,jpg
```

七牛云存储保存demo:

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
