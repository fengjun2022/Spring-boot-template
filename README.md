# spring-boot-security-template



## 运行条件

 Java8 
JDK1.8

使用框架之前请先执行 项目内的 security.sql文件


## 技术架构
> SpringBoot，SpringbootSecurity，mysql8.0及以上

## 快速开始
这套框架旨在解决，垂直与横行越权漏洞，用户权限登录等问题
框架自带一套方法级别权限系统，与登录注册
### 1.如何配置白名单与权限名单，你可以在 security.yml设置登录白名单与基于根路径的权限白名单
示例：

```yml
security:
  permit-all: # 权限访问白名单

   - "/login"
     - "/user/**"
       role-based: # 需要权限名单
         - pattern: "/admin/**"
           role: ADMIN
         - pattern: "/manager/**"
           role: MANAGER
```


### 2.如何使用系统的登录

接口：/login

在CustomAuthenticationFilter文件中配置了setFilterProcessesUrl("/login");
此时前端只需要调用 ip:端口/login 就可以调用登录接口，请确保接口在接口白名单内
你也可以配置多个登录只需要在CustomAuthenticationFilter方法内配置多个URL即可
示例：

```java
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
       super(authenticationManager);
       this.authenticationManager = authenticationManager;
       // 设置自定义处理登录请求的 URL
       setFilterProcessesUrl("/login");
       setFilterProcessesUrl("/login-admin");

   }
```



### 3.调用注册接口

接口：/user/register

系统自定义的注册接口在UserController可以看到，，你可以修改这个方法已达到你想要的业务内容，系统使用securePasswordEncryptorUtil.encryptPassword(user.getPassword())进行密码加密。
encryptPassword返回的是一个线程，你可以使用join等待
示例：

```java
    public void register(UserEntity user) {
        CompletableFuture<String> stringCompletableFuture = securePasswordEncryptorUtil.encryptPassword(user.getPassword());
        String password = stringCompletableFuture.join();
        user.setPassword(password);
        userMapper.register(user);
    }
```

**securePasswordEncryptorUtil.encryptPassword**方法 ：

形参1: 加密的内容

形参2：加密算法（ bcrypt, pbkdf2，Argon2PasswordEncoder，StandardPasswordEncoder，SCryptPasswordEncoder）

在你不增加任何业务的情况下，此接口已经实现用户注册

### 4.方法级别权限
框架默认实现了方法级别权限系统
方法级权限控制允许我们在方法执行前后进行权限验证，从而对业务逻辑实现细粒度的保护。Spring Security 提供了多种注解来实现方法级权限控制，下面介绍几种常用的注解及示例。
默认启用了 

```java
@EnableGlobalMethodSecurity(    
        prePostEnabled = true,   // 启用 @PreAuthorize 和 @PostAuthorize
        securedEnabled = true,     // 启用 @Secured
        jsr250Enabled = true   )    // 启用 @RolesAllowed)
        以上三种注解
```



 #### @PreAuthorize
​    **•   说明：在方法调用前进行权限验证，可以使用 Spring Expression Language（SpEL）表达式，判断当前用户是否具有某些角色或权限。**
示例：

```java
@Service
public class UserService {

   // 只有拥有 ADMIN 角色的用户才能调用此方法
   @PreAuthorize("hasRole('ADMIN')")
  public void deleteUser(Long userId) {
       // 删除用户的业务逻辑
   }

   // 根据方法参数和当前用户信息进行判断
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public void updateUser(Long userId, UserDto userDto) {
        // 更新用户信息
   }
}    
```



#### @PostAuthorize

  **说明：在方法执行后对返回结果进行验证，适用于需要基于返回结果内容进行权限判断的场景。**

```java


@Service
public class DocumentService {

    // 获取文档后，检查返回的文档所有者是否为当前用户
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long docId) {
        // 查询文档逻辑
        return documentRepository.findById(docId);
    }
}
```



 #### @Secured
**说明：只支持简单的角色判断，不支持复杂表达式。**

```java
@Service
public class ReportService {

   // 只有拥有 ROLE_MANAGER 的用户才能访问此方法
    @Secured("ROLE_MANAGER")
    public void generateReport() {
       // 生成报告逻辑
   }
}
```



 ####  @PreFilter 与 @PostFilter
说明：这两个注解适用于对集合进行过滤，分别在方法调用前和调用后对集合中的元素进行权限判断。

```java
@Service
public class OrderService {

   // 在方法调用前对输入的订单列表进行过滤，只保留当前用户有权限处理的订单ID
   @PreFilter("filterObject.owner == authentication.name")
   public void processOrders(List<Order> orders) {
        // 处理订单逻辑，orders 已经过滤过
    }

   // 在方法调用后，对返回的订单列表进行过滤，只返回当前用户有权限查看的订单
    @PostFilter("filterObject.owner == authentication.name")
    public List<Order> getOrders() {
        // 查询所有订单
        return orderRepository.findAll();
    }
}
```

#### 注意事项

**统一命名规范**：

Spring Security 默认要求角色名称以 ROLE_ 开头。但在数据库中，可以选择只存储角色核心名称（例如 ADMIN、USER），在加载时动态拼接 ROLE_ 前缀；或者直接存储全名（例如 ROLE_ADMIN）。关键是整个系统必须保持一致。

**建议**：推荐在数据库中只存储角色核心名称，加载用户详情时统一添加前缀，降低冗余性并避免混淆。

**命名规范**：

保持角色名称全部大写，使用下划线分隔（例如：ADMIN_MANAGER），这样更直观且符合大多数安全框架的风格。



### 使用雪花ID作为主键进行存储

框架通过自定义 MyBatis 拦截器与注解自动生成雪花ID，进而在数据库插入操作时为主键字段自动赋值。该方案封装了雪花ID生成的逻辑，只需在对应的 Mapper 方法上添加注解 @AutoGenerateSnowflakeId，系统会在执行 SQL 前自动为对象填充唯一主键。

例如：

```java
  @AutoGenerateSnowflakeId
    @Insert("INSERT INTO tem.user (id, username, password, authorities) VALUES (#{id}, #{username}, #{password}, #{authorities})")
    void register(UserEntity user);
```

  @AutoGenerateSnowflakeId的默认参数是id，你可以手动指定其他的主键字段 @AutoGenerateSnowflakeId("userId")

**方案说明**



**1. 注解定义**

​	**注解名称**：@AutoGenerateSnowflakeId

​	**功能**：标记需要自动生成雪花ID的 Mapper 方法。

​	**字段设置**：支持通过 fieldName 属性指定需要赋值的字段名称，默认为 "id"。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoGenerateSnowflakeId {
    /**
     * 指定需要赋值的字段名称，默认为 "id"
     */
    String fieldName() default "id";
}
```

### Token 配置

你可以在yml内配置以下的参数

```yml
jwt:
    # jwt secretKey
  secret-key: nangtongcourtjj1001001
    # token过期时间 毫秒
  ttl: 28800000
    # 请求头
  head-name: Authorization
    # token 认证头
  head-base: "Bearer "
```



你可以在 CustomAuthenticationFilter 内添加你想添加的内容加入到token内，**请注意不要删除原有的添加内容，可能会导致某些功能不可用**

```java
        // 生成 JWT Token（可在 token 中附加更多自定义信息，如角色、权限等）
        String token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getTtl()))
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes()));
```



### UserRepository 查询用户

你可以继承 UserRepository 进行用户查询请注意代码内已经默认实现了UserRepository方法在UserRepositoryImpl内，该方法非常重要影响整个系统的运行



## 协作者

> 冯俊