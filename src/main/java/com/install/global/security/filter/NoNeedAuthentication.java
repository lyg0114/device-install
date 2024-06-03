package com.install.global.security.filter;


import static com.install.global.security.filter.CustomAuthenticationFilter.LOGIN_PATH;

import jakarta.servlet.http.HttpServletRequest;

public class NoNeedAuthentication {

  public static boolean isNoNeedAuthenticationURL(HttpServletRequest request) {
    return request.getPathInfo().equals(LOGIN_PATH);
  }
}
