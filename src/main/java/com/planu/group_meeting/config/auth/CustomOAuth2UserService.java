package com.planu.group_meeting.config.auth;

import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.entity.common.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserDAO userDAO;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        System.out.println("sssssssssssssssssssssssssssssssss");
        OAuth2User oAuth2User = super.loadUser(request);
        KakaoResponse response = new KakaoResponse(oAuth2User.getAttributes());

        User user = saveUser(response);
        return new CustomOAuth2UserDetails(user);

    }

    private User saveUser(KakaoResponse response) {
        String username = response.getProvider() + "_" + response.getProviderId();
        User findUser = userDAO.findByUsername(username);
        if(findUser==null){
            User createUser = User.builder()
                    .username(username)
                    .password("kakao")
                    .name(response.getName())
                    .email(response.getEmail())
                    .role(Role.ROLE_GUEST)
                    .build();
            userDAO.insertUser(createUser);
            return createUser;
        }
        return findUser;
    }
}
