package myblog.blog.member.application;

@Deprecated
public class BuildAdminAccountHelper {

    // 앱 구동시 ADMIN 계정 Insert
//    @Value("${admin.username}")
//    private String adminUsername;
//    @Value("${admin.picUrl}")
//    private String adminPicUrl;
//    @Value("${admin.email}")
//    private String adminEmail;
//    @Value("${admin.providerId}")
//    private String adminProviderId;
//    @Value("${admin.provider}")
//    private String adminProvider;
    /*
        - 앱 구동시 ADMIN 계정 INSERT
    */
//    @PostConstruct
//    public void insertAdmin(){
//
//        Member admin = memberRepository.findByEmail(adminEmail);
//        if(admin == null){
//            admin = Member.builder()
//                    .username(adminUsername+"#"+adminProviderId.substring(0,5))
//                    .email(adminEmail)
//                    .picUrl(adminPicUrl)
//                    .userId(adminProviderId)
//                    .providerId(adminProviderId)
//                    .provider(adminProvider)
//                    .role(Role.ADMIN)
//                    .build();
//
//            memberRepository.save(admin);
//        }
//    }
}
