<template >
  <div class="login-container" v-bind:style="loginBg" >
    <el-form autoComplete="on" :model="loginForm" :rules="loginRules" ref="loginForm" label-position="left"
             label-width="0px"
             class="card-box login-form">
      <div style="text-align:right;">
        <lang-select class="set-language"/>
      </div>

      <h3 class="title">{{ $t('login.title') }}</h3>


      <el-form-item prop="username">
        <span class="svg-container svg-container_login">
          <svg-icon icon-class="user" />
        </span>
        <el-input v-model="loginForm.username" autoComplete="off" />
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" ></svg-icon>
        </span>
        <el-input type="password" @keyup.enter.native="handleLogin" v-model="loginForm.password"
                  autoComplete="off"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:100%;" :loading="loading" @click.native.prevent="handleLogin">
          {{ $t('login.logIn') }}
        </el-button>
      </el-form-item>
      <div class="registerBtn"><svg-icon icon-class="user"/>
        <router-link to="/register"><a>{{ $t('common.toRegister')}}</a></router-link>
      </div>

      <!--<div class="tips">-->
        <!--<span style="margin-right:36px;">{{ $t('login.username') }} : admin</span>-->
      <!--</div>-->
      <!--<div class="tips">-->
        <!--<span style="margin-right:51px;">{{ $t('login.username') }} : test</span>-->
      <!--</div>-->
      <!--<div class="tips">-->
        <!--<span style="margin-right:28px;">{{ $t('login.username') }} : finance</span>-->
      <!--</div>-->
      <!--<div class="tips">-->
        <!--<span style="margin-right:18px;">{{ $t('login.username') }} : xiaoming</span>-->
      <!--</div>-->
      <!--<div class="tips">-->
        <!--<span style="margin-right:21px;">{{ $t('login.username') }} : xiaofang</span>-->
      <!--</div>-->
    </el-form>
  </div>
</template>
<script>
  import LangSelect from '@/components/LangSelect'
  import loginBgPic from '@/assets/common/login_bg.jpg'
  export default {
    name: 'login',
    components: { LangSelect },
    data() {
      return {
        // ll: 'this.src="' + require('@/assets/common/login_bg.jpg') + '"',
        // ee: 'https://07033320a.github.io/images/login_bg.JPG',
        loginBg:"background-image: url('https://07033320a.github.io/images/login_bg.JPG') ",
        loginForm: {
          username: '',
          password: ''
        },
        loginRules: {
          username: [{required: true, trigger: 'blur', message: "请输入用户名"}],
          password: [{required: true, trigger: 'blur', message: "请输入密码"}]
        },
        loading: false
      }
    },
    methods: {
      handleLogin() {
        this.$refs.loginForm.validate(valid => {
          if (valid) {
            this.loading = true
            this.$store.dispatch('Login', this.loginForm).then(data => {
              this.loading = false
              if ("success" === data.result) {
                this.$router.push({path: '/'})
              } else {
                let errorMessage = "账号/密码错误";
                if(data['errorMessage']){
                  errorMessage = data['errorMessage'];
                }
                this.$message.error(errorMessage);
              }
            }).catch(() => {
              this.loading = false
            })
          } else {
            return false
          }
        })
      }
    }
  }
</script>
<style rel="stylesheet/scss" lang="scss">
  @import "../../styles/mixin.scss";
  $bg: #2d3a4b;
  $dark_gray: #889aa4;
  $light_gray: black;

  .login-container {
    @include relative;
    height: 100vh;
    background-size: 100% 100%;
    -moz-background-size: 100% 100%;
    //background: url(https://harsima.github.io/vue-backend/static/images/default/login.caef9df.jpg) 50% no-repeat;
    input:-webkit-autofill {
      -webkit-box-shadow: 0 0 0px 1000px #e5e5e5 inset !important;
      -webkit-text-fill-color: black !important;
    }
    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
    }
    .el-input {
      display: inline-block;
      height: 47px;
      width: 85%;
    }
    .tips {
      font-size: 14px;
      color: gray;
      margin-bottom: 10px;
    }
    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;
      &_login {
        font-size: 20px;
      }
    }
    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
    .login-form {
      position: absolute;
      background-color: white;
      left: 0;
      right: 0;
      width: 400px;
      padding: 35px 35px 15px 35px;
      margin: 120px auto;
    }
    .el-form-item {
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
      border-radius: 5px;
      color: #454545;
    }
    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
    }
    .thirdparty-button {
      position: absolute;
      right: 35px;
      bottom: 28px;
    }
    .registerBtn{
      text-align: right;
      color: $light_gray;
    }
  }
</style>
