<template>
  <div class="course-student-container">
    <div class="filter-container">
      <el-form :model="listQuery" ref="listQuery">
        <el-form-item prop="content">
          <el-input class="filter-item" :placeholder="$t('teacher.courseNameNoDetail')" v-model="listQuery.content"
                    size="small" v-if="hasPerm('course-student:list')" ref="searchBtn" style="width: 200px;"
                    @keyup.enter.native="handleFilter" clearable/>
          <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" v-if="hasPerm('course-student:list')" @click="handleFilter">{{ $t('table.search') }}</el-button>

          <!--<el-button class="filter-item" size="small" style="margin-left: 0px;" @click="resetForm('listQuery')">重置</el-button>-->
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="list" v-loading.body="listLoading" height="480" element-loading-text="" border fit
              highlight-current-row>
      <el-table-column align="center" :label="$t('table.id')" width="80">
        <template slot-scope="scope">
          <span v-text="getIndex(scope.$index)"> </span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="content" :label="$t('teacher.courseName')" style="width: 60px;">
        <template v-if="scope.row.content!=null" slot-scope="scope">
          {{scope.row.content}}
          <el-popover class="col-el-popover"
                      placement="top-start"
                      width="400"
                      trigger="click">
            {{scope.row.brief}}
            <i slot="reference" v-if="scope.row.brief!=null" class="el-icon-share" style="cursor: pointer;"></i>
          </el-popover>
        </template>
      </el-table-column>

      <el-table-column align="center" prop="capacity" v-if="isMySelect == 'false' " :label="$t('teacher.remainNum')" style="width: 60px;">
        <template slot-scope="scope">
          <span style="color: green;">{{Number(scope.row.capacity)-Number(scope.row.pickStudentNum)}}</span>
        </template>

      </el-table-column>
      <!--<el-table-column align="center" prop="grade" label="授课年级" style="width: 60px;">-->
        <!--<template slot-scope="scope">-->
          <!--{{formatGrade(scope.row.grade)}}-->
        <!--</template>-->
      <!--</el-table-column>-->
      <!--<el-table-column align="center" :label="$t('teacher.tuition')" style="width: 60px;">-->
        <!--<template slot-scope="scope" >-->
          <!--<div v-if="scope.row.tuitionType=='fee'">-->
            <!--{{scope.row.tuition}} RMB-->
            <!--<span v-if="scope.row.tuitionSubType == '1'">{{$t('teacher.tuitionOption1')}}</span>-->
            <!--<span v-if="scope.row.tuitionSubType == '2'">{{$t('teacher.tuitionOption2')}}</span>-->
            <!--<span v-if="scope.row.tuitionSubType == '3'">{{$t('teacher.tuitionOption3')}}</span>-->
          <!--</div>-->
          <!--<div v-if="scope.row.tuitionType=='free'">{{$t('teacher.tuitionFree')}}</div>-->

        <!--</template>-->
      <!--</el-table-column>-->
      <el-table-column align="center" prop="finalTuition" :label="$t('teacher.tuition')" style="width: 60px;">
      </el-table-column>
      <el-table-column align="center" prop="needTrainingAid" :label="$t('teacher.needSelfMaterial')" style="width: 60px;">
        <template slot-scope="scope" v-if="scope.row.needTrainingAid!=null">
          &nbsp;
          <span v-if="scope.row.needTrainingAid == '1'">{{$t('teacher.yes')}}</span>
          <span v-if="scope.row.needTrainingAid == '2'">{{$t('teacher.no')}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="price" :render-header="materialPrice" width="175">
      </el-table-column>
      <el-table-column align="center" prop="courseDateStudent" :label="$t('teacher.courseDate')" v-if="isMySelect == 'true' " style="width: 60px;">
        <template slot-scope="scope" v-if="scope.row.courseDateStudent!=null">
          &nbsp;
          <span v-if="scope.row.courseDateStudent && scope.row.courseDateStudent.indexOf && scope.row.courseDateStudent.indexOf('tue')>-1">{{$t('week.tue')}}</span>
          <span v-if="scope.row.courseDateStudent && scope.row.courseDateStudent.indexOf && scope.row.courseDateStudent.indexOf('wed')>-1">{{$t('week.wed')}}</span>
          <span v-if="scope.row.courseDateStudent && scope.row.courseDateStudent.indexOf && scope.row.courseDateStudent.indexOf('thu')>-1">{{$t('week.thu')}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="courseDate" :label="$t('teacher.courseDate')" v-if="isMySelect == 'false'" style="width: 60px;">
        <template slot-scope="scope" v-if="scope.row.courseDate!=null">
          &nbsp;
          <span v-if="scope.row.courseDate && scope.row.courseDate.indexOf && scope.row.courseDate.indexOf('tue')>-1">{{$t('week.tue')}}</span>
          <span v-if="scope.row.courseDate && scope.row.courseDate.indexOf && scope.row.courseDate.indexOf('wed')>-1">{{$t('week.wed')}}</span>
          <span v-if="scope.row.courseDate && scope.row.courseDate.indexOf && scope.row.courseDate.indexOf('thu')>-1">{{$t('week.thu')}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="courseArea" :label="$t('teacher.courseArea')"style="width: 150px;">
      </el-table-column>
      <el-table-column align="center" prop="nickname" :label="$t('teacher.teacherName')" style="width: 60px;"></el-table-column>
      <el-table-column align="center" prop="comments" :label="$t('student.notice')" style="width: 60px;"></el-table-column>

      <el-table-column align="center" prop="origin_fileName" :label="$t('teacher.attachment')" >
        <template slot-scope="scope">
          <a style="text-decoration: underline;color: #409EFF;" @click="downloadFromList(scope.row.attachId)">{{scope.row.originFileName}}</a>
        </template>
      </el-table-column>

      <el-table-column align="center" prop="origin_fileName" v-if="isMySelect == 'true'" :label="$t('student.payAttach')" >
        <template slot-scope="scope">
          <a style="text-decoration: underline;color: #409EFF;" @click="downloadFromList(scope.row.attachIdStu)">{{scope.row.originFileNameStu}}</a>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('table.actions')" width="200" v-if="getGroupTag()=='-1' || (hasPerm('course-student:update') && (getPeriod('canPick') || getPeriod('canFee')))">
        <template slot-scope="scope">
          <el-button type="primary" icon="edit" size="small" v-if="(Number(scope.row.capacity)-Number(scope.row.pickStudentNum))>0 && (getGroupTag()=='-1' || (isMySelect == 'false' && getPeriod('canPick'))) " @click="showUpdate(scope.$index,'pick')">{{$t('student.pickCourse')}}</el-button>
          <el-tag type="danger" v-if="(Number(scope.row.capacity)-Number(scope.row.pickStudentNum))==0 ">名额已满</el-tag>
          <el-button type="primary" icon="edit" size="small" v-if="getGroupTag()=='-1' || (isMySelect == 'true' && getPeriod('canFee') && !(scope.row.finalTuition && scope.row.finalTuition.indexOf('free')>-1))" @click="showUpdate(scope.$index,'fee')">
            <template >
              <span v-if="scope.row.isPay=='1'">已支付</span>
              <span v-else>{{$t('student.pay')}}</span>
            </template>

          </el-button>

          <el-popover v-if="isMySelect == 'true' || getGroupTag() == '-1'"
            placement="top"
            trigger="click"
            width="160">
            <p>{{$t('table.deleteConfirm')}}</p>
            <div style="text-align: center; margin: 0">
              <el-button type="primary" size="mini" @click="deleteMyCourse(scope.row.id,scope.row.attachIdStu)">{{$t('table.confirm')}}</el-button>
            </div>
            <el-button type="danger" icon="edit" size="small" slot="reference" v-if="getGroupTag()=='-1' || (hasPerm('course-student:update') )">{{$t('student.revoke')}}</el-button>
          </el-popover>

        </template>
      </el-table-column>

    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="listQuery.pageNum"
      :page-size="listQuery.pageRow"
      :total="totalCount"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>


    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :before-close="beforeClose">
      <el-row :gutter="24">
        <el-form class="small-space" :model="tempCourse" label-position="left" label-width="100px"
                 style='width: 500px; margin-left:50px;'>
          <el-form-item :label="$t('teacher.courseNameNoDetail')">
            <span>{{tempCourse.content}}</span>
          </el-form-item>
          <div v-if="type=='pick'">
            <el-form-item :label="$t('teacher.courseDate')">
              <course-date v-bind:dataVal="tempCourse.courseDate"
                           v-bind:dataArr="tempCourse.courseDateArr"

                           v-bind:dataCheckAll="checkAll"
                           v-bind:dataIsIndeterminate="isIndeterminate"
                           v-bind:courseDateOptions="courseDateOptions"
                           v-on:changeCourseDate="changeCourseDate"
                           ref="courseDate"></course-date>
            </el-form-item>
            <el-form-item>
              <span style="color:red;font-weight: bolder;">注意：请于选课日期截止后三日内进行缴费，并上传截图，逾期缴费会导致选课失败，望知悉。</span>
            </el-form-item>
          </div>
          <div v-if="type=='fee'">
            <el-form-item :label="$t('student.payAttach')">
              <file-uploader v-bind:dataVal="tempCourse.attachIdStu" v-bind:fileListArr="fileList"
                             v-bind:businessId="tempCourse.id"
                             v-on:fileChangeToFather="fileChangeToFather"
                             v-bind:businessType="attachBusinessType"
                             ref="fileUploader"
              ></file-uploader>
            </el-form-item>
          </div>
        </el-form>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{$t('table.cancel')}}</el-button>
        <el-button type="success" v-bind:disabled="selectBtnDisabled" @click="selectCourse" v-if="type=='pick'">{{$t('student.select')}}</el-button>
        <el-button type="success" v-bind:disabled="selectBtnDisabled" @click="feeCourse" v-if="type=='fee'">{{$t('student.confirmHasPay')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import CourseDate from '../CourseTeacher/components/CourseDate';
  import FileUploader from '../CourseTeacher/components/FileUploader';
  import store from '../../store'
  export default {
    name: 'student-table',
    props:['listUrl','isMySelect'],
    components: {
     CourseDate,FileUploader
    },
    data() {
      return {
        mySelfList: this.$props['isMySelect'],
        selectBtnDisabled: false,
        type: 'pick',
        attachBusinessType: 'course-student',
        totalCount: 0, //分页组件--数据总条数
        list: [],//表格的数据
        fileList: [],//上传文件list
        listLoading: false,//数据加载等待动画
        listQuery: {
          studentCanPick: true,
          pageNum: 1,//页码
          pageRow: 50,//每页条数
          name: ''
        },
        courseDateOptions: [],
        dialogStatus: 'create',
        dialogFormVisible: false,
        textMap: {
          update: this.$t('student.pickCourse'),
          create: this.$t('teacher.createCourse'),
          pay: this.$t('student.pay')
        },
        checkAll: false,
        isIndeterminate: true,
        tempCourse: {
          id: "",
          courseDateArr:[],
          courseDate: 0,
          content: '',
          attachIdStu: ""
        }
      }
    },
    created() {
      this.getList();
    },
    methods: {
      materialPrice(h, { column, $index }){
        if(store.getters.language == '' || store.getters.language =='en'){
          return (
            <div>
            <span> materials price ¥ </span>
          <el-tooltip class="item" effect="dark" content="If you need to purchase the product uniformly, please contact the external teacher."
          placement="bottom">
            <i class="el-icon-warning table-msg" style='color:#9d1f24;'></i>
            </el-tooltip>
            </div>
        )

        }else{
          return (
            <div>
            <span> 教具参考价格¥ </span>
            <el-tooltip class="item" effect="dark" content="如需统一购买，请与外聘老师联系"  placement="bottom">
            <i class="el-icon-warning table-msg" style='color:#9d1f24;'></i>
            </el-tooltip>
            </div>
        )
        }


      },


      changeCourseDate(val){
        this.tempCourse.courseDateArr = val;
        if(val==''){
          this.selectBtnDisabled = true;
        }else{
          this.selectBtnDisabled = false;
        }
      },
      downloadFromList(obj){
        this.previewUploadFile({url:obj});
      },
      previewUploadFile(obj){
        this.api({
          url: "/sys/download/"+obj['url'],
          method: "get",
          // responseType: 'blob'
        }).then(response  => {
          this.$message.success("下载成功！");
      });
      },
      formatGrade(arrVal){
        function formatSingle(val){
          let resultVal = '';
          if(val == 0){
            resultVal = 'KG'
          }else{
            resultVal = 'G'+String(val);
          }
          return resultVal;
        }
        if(arrVal!=undefined){
          return formatSingle(arrVal[0]) +'--'+ formatSingle(arrVal[1]);
        }
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
        this.getList();
      },
      handleFilter(){
        this.listQuery.pageNum = 1;
        this.getList();
      },
      getList() {
        //查询列表
        if (!this.hasPerm('course-student:list')) {
          return
        }
        this.listLoading = true;
        this.api({
          url: this.$props['listUrl'],
          method: "get",
          params: this.listQuery
        }).then(data => {
          this.listLoading = false;
        this.list = data.list;
        this.totalCount = data.totalCount;
          this.$emit('getTotal',this.totalCount);
      })
      },
      handleSizeChange(val) {
        //改变每页数量
        this.listQuery.pageRow = val;
        this.handleFilter();
      },
      handleCurrentChange(val) {
        //改变页码
        this.listQuery.pageNum = val
        this.getList();
      },
      getIndex($index) {
        //表格序号
        return (this.listQuery.pageNum - 1) * this.listQuery.pageRow + $index + 1
      },
      showUpdate($index,type) {
        //显示修改对话框
        this.type = type;
        if(type=='pick'){
          this.tempCourse = this.list[$index];
          this.tempCourse.courseId = this.list[$index].id;
          if(this.$refs['courseDate']){
            if(this.list[$index].courseDate == 'tue'){
              this.$refs['courseDate']['courseDateOptions'][0]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][1]['disabled'] = true;
              this.$refs['courseDate']['courseDateOptions'][2]['disabled'] = true;
              this.$refs['courseDate']['courseDate'] = 1;
              this.$refs['courseDate']['courseDateArr'] = ['tue']
            }else if(this.list[$index].courseDate == 'wed'){
              this.$refs['courseDate']['courseDateOptions'][0]['disabled'] = true;
              this.$refs['courseDate']['courseDateOptions'][1]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][2]['disabled'] = true;
              this.$refs['courseDate']['courseDate'] = 2;
              this.$refs['courseDate']['courseDateArr'] = ['wed']
            }else if(this.list[$index].courseDate == 'tue,wed'){
              this.$refs['courseDate']['courseDateOptions'][0]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][1]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][2]['disabled'] = true;
              this.$refs['courseDate']['courseDate'] = 3;
              this.$refs['courseDate']['courseDateArr'] = ['tue','wed']
            }else if(this.list[$index].courseDate == 'thu'){
              this.$refs['courseDate']['courseDateOptions'][0]['disabled'] = true;
              this.$refs['courseDate']['courseDateOptions'][1]['disabled'] = true;
              this.$refs['courseDate']['courseDateOptions'][2]['disabled'] = false;
              this.$refs['courseDate']['courseDate'] = 4;
              this.$refs['courseDate']['courseDateArr'] = ['thu']
            }else if(this.list[$index].courseDate == 'tue,thu'){
              this.$refs['courseDate']['courseDateOptions'][0]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][1]['disabled'] = true;
              this.$refs['courseDate']['courseDateOptions'][2]['disabled'] = false;
              this.$refs['courseDate']['courseDate'] = 5;
              this.$refs['courseDate']['courseDateArr'] = ['tue','thu']
            }else if(this.list[$index].courseDate == 'wed,thu'){
              this.$refs['courseDate']['courseDateOptions'][0]['disabled'] = true;
              this.$refs['courseDate']['courseDateOptions'][1]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][2]['disabled'] = false;
              this.$refs['courseDate']['courseDate'] = 6;
              this.$refs['courseDate']['courseDateArr'] = ['wed','thu']
            }else{
              this.$refs['courseDate']['courseDateOptions'][0]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][1]['disabled'] = false;
              this.$refs['courseDate']['courseDateOptions'][2]['disabled'] = false;
              this.$refs['courseDate']['courseDate'] = 7;
              this.$refs['courseDate']['courseDateArr'] = ['tue','wed','thu']
            }
          }else{
            let courseDateOptionsArr = [{label: 'tue',disabled: false}, {label:'wed',disabled: false}, {label:'thu',disabled: false}];
            if(this.list[$index].courseDate == 'tue'){
              courseDateOptionsArr[1]['disabled'] = true;
              courseDateOptionsArr[2]['disabled'] = true;
              this.tempCourse.courseDate = 1;
              this.tempCourse.courseDateArr = ['tue'];
            }else if(this.list[$index].courseDate == 'wed'){
              courseDateOptionsArr[0]['disabled'] = true;
              courseDateOptionsArr[2]['disabled'] = true;
              this.tempCourse.courseDate = 2;
              this.tempCourse.courseDateArr = ['wed'];
            }else if(this.list[$index].courseDate == 'tue,wed'){
              courseDateOptionsArr[2]['disabled'] = true;
              this.tempCourse.courseDate = 3;
              this.tempCourse.courseDateArr = ['tue','wed'];
            }else if(this.list[$index].courseDate == 'thu'){
              courseDateOptionsArr[0]['disabled'] = true;
              courseDateOptionsArr[1]['disabled'] = true;
              this.tempCourse.courseDate = 4;
              this.tempCourse.courseDateArr = ['thu'];
            }else if(this.list[$index].courseDate == 'tue,thu'){
              courseDateOptionsArr[1]['disabled'] = true;
              this.tempCourse.courseDate = 5;
              this.tempCourse.courseDateArr = ['tue','thu'];
            }else if(this.list[$index].courseDate == 'wed,thu'){
              courseDateOptionsArr[0]['disabled'] = true;
              this.tempCourse.courseDate = 6;
              this.tempCourse.courseDateArr = ['wed','thu'];
            }else{
              courseDateOptionsArr[0]['disabled'] = false;
              courseDateOptionsArr[1]['disabled'] = false;
              courseDateOptionsArr[2]['disabled'] = false;
              this.tempCourse.courseDate = 7;
              this.tempCourse.courseDateArr = ['tue','wed','thu'];
            }
            this.courseDateOptions = courseDateOptionsArr;
          }
          this.dialogStatus = "update"
        }else{
          this.fileList = [];
          if(this.$refs['fileUploader']){
            this.$refs['fileUploader']['fileList'] = this.fileList
          }

          this.selectBtnDisabled = true;
          this.tempCourse.courseId = this.list[$index].courseId;
          this.tempCourse.id = this.list[$index].id;
          this.tempCourse.content = this.list[$index].content;
          this.tempCourse.attachIdStu = this.list[$index].attachIdStu;
          this.tempCourse.originFileNameStu = this.list[$index].originFileNameStu;

          if(this.tempCourse.attachIdStu && this.tempCourse.attachIdStu!=''){
            this.fileList.push({name: this.tempCourse.originFileNameStu, url: this.tempCourse.attachIdStu})
            this.selectBtnDisabled = false;
          }
          if(this.$refs['fileUploader']){
            this.$refs['fileUploader']['fileList'] = this.fileList
          }
          this.dialogStatus = "pay"
        }


        this.dialogFormVisible = true
      },
      fileChangeToFather(childFileList){
        this.fileList = childFileList;
        this.selectBtnDisabled = !(this.fileList.length>0);
      },
      selectCourse(){
        this.tempCourse.courseDate = this.$refs['courseDate']['courseDate'];
        this.courseDateArr = this.$refs['courseDate']['courseDateArr'];
        this.tempCourse.courseDateArr = this.$refs['courseDate']['courseDateArr'];

        this.api({
          url: "/course-student/pickCourse",
          method: "post",
          data: this.tempCourse
        }).then(() => {
          this.getList();
          this.$emit('changeTab',event.target,'0');
          this.dialogFormVisible = false
        });
      },
      feeCourse(){

        this.api({
          url: "/course-student/feeCourse",
          method: "post",
          data: this.tempCourse
        }).then(() => {
          this.getList();
        this.$emit('changeTab',event.target,'0');
        this.dialogFormVisible = false
      });
      },
      deleteMyCourse(tmpId,attachIdStu) {
        //删除选中课程
        this.api({
          url: "/course-student/deleteCourse",
          method: "post",
          data: {id: tmpId,attachIdStu:attachIdStu}
        }).then(() => {
          //this.$refs['searchBtn'].focus();
          this.$message.success(this.$t('common.deleteSuccess'));
        this.getList();
        this.dialogFormVisible = false;
      })
      },
      beforeClose(){
        this.dialogFormVisible = false;
        this.api({
          url: this.$props['listUrl'],
          method: "get",
          params: this.listQuery
        }).then(data => {
          this.list = data.list;
          this.totalCount = data.totalCount;
        })
      }
    }
  }
</script>
<style scoped>
  .el-input-number__increase{
    right: 11px;
  }
  .el-form-item__content .el-select, .el-form-item__content .el-input-number{
    width:102% !important;
  }
</style>

