package pers.acp.admin.token.granter

import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.stereotype.Component
import pers.acp.admin.component.UserPasswordEncrypt
import pers.acp.admin.security.SecurityUserDetailsService
import pers.acp.admin.token.UserPasswordAuthenticationToken
import pers.acp.admin.token.error.CustomerOAuth2Exception

@Component
class UserPasswordAuthenticationProvider(
    private val userDetailsService: SecurityUserDetailsService,
    private val userPasswordEncrypt: UserPasswordEncrypt
) : AuthenticationProvider, MessageSourceAware {
    private var messages: MessageSourceAccessor = SpringSecurityMessageSource.getAccessor()
    override fun authenticate(authentication: Authentication?): Authentication? {
        if (!supports(authentication?.javaClass)) {
            return null
        }
        return authentication!!.name?.let { username ->
            userDetailsService.loadUserByUsername(username).let { user ->
                if (!user.isEnabled) {
                    throw CustomerOAuth2Exception("用户已被锁定或禁用！")
                }
                authentication.credentials?.toString()?.let { password ->
                    if (!userPasswordEncrypt.matches(password, user.password)) {
                        throw CustomerOAuth2Exception("用户名或密码不正确！")
                    }
                    UserPasswordAuthenticationToken(username, null, user.authorities).apply {
                        this.isAuthenticated = user.authorities.isNotEmpty()
                        this.details = authentication.details
                    }
                } ?: throw CustomerOAuth2Exception("密码不能为空！")
            }
        } ?: throw CustomerOAuth2Exception("用户名不能为空！")
    }

    override fun supports(authentication: Class<*>?): Boolean = authentication?.let {
        UserPasswordAuthenticationToken::class.java.isAssignableFrom(it)
    } ?: false

    override fun setMessageSource(messageSource: MessageSource) {
        messages = MessageSourceAccessor(messageSource)
    }
}