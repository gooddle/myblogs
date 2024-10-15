package com.teamsparta.myblog.domain.user.service


import com.teamsparta.myblog.infra.RedisUtils
import com.teamsparta.myblog.domain.exception.ModelNotFoundException
import com.teamsparta.myblog.domain.user.dto.LoginRequest
import com.teamsparta.myblog.domain.user.dto.LoginResponse
import com.teamsparta.myblog.domain.user.dto.SignUpRequest
import com.teamsparta.myblog.domain.user.dto.UserResponse
import com.teamsparta.myblog.domain.user.model.Role
import com.teamsparta.myblog.domain.user.model.User
import com.teamsparta.myblog.domain.user.model.toResponse
import com.teamsparta.myblog.domain.user.repository.UserRepository
import com.teamsparta.myblog.infra.security.jwt.JwtPlugin
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin,
    private val redisUtils: RedisUtils,

    ): UserService {
    override fun loginUser(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw ModelNotFoundException("user")

        if (!passwordEncoder.matches(request.password, user.password))
            throw IllegalStateException("비밀번호가 틀립니다.")

        return LoginResponse(
            token = jwtPlugin.generateAccessToken(
                subject = user.id.toString(),
                email = user.email,
                role = Role.USER.name
            )
        )
    }

    @Transactional
    override fun signUpUser(request: SignUpRequest): UserResponse {
        checkEmail(request.email,request.emailCode)

        if (userRepository.existsByEmail(request.email))
            throw IllegalStateException("이미 사용중인 이름입니다.")

        if (request.password.contains(request.email))
            throw IllegalStateException("비밀번호와 닉네임은 동일하거나 포함될 수 없습니다.")

        redisUtils.deleteData(request.emailCode)


        return userRepository.save(
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                role = Role.USER
            )
        ).toResponse()

    }


   private fun checkEmail(email:String,codeNumber: String):Boolean {
        val storedCode = redisUtils.getData(email)
        if(storedCode.isNullOrEmpty()||storedCode != codeNumber)throw IllegalStateException("인증번호 혹은 동일한 이메일을 사용해주세요")

        return true
    }



}