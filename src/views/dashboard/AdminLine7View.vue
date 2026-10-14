<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { adminLine7Api } from '../../services/endpoints'
import { apiError } from '../../services/api'
const route=useRoute(),router=useRouter(),type=computed(()=>route.params.type==='trains'?'trains':'stations'),isTrain=computed(()=>type.value==='trains'),id=computed(()=>route.params.id),editing=computed(()=>Boolean(id.value)),creating=computed(()=>route.path.endsWith('/new')),title=computed(()=>isTrain.value?'열차':'역'),items=ref([]),totalCount=ref(0),page=ref(1),size=10,field=ref(isTrain.value?'trainNo':'stationName'),keyword=ref(''),error=ref(''),saving=ref(false),record=reactive({lineId:'',trainNo:'',section:'',driver:'',direction:'UP',status:'RUNNING',active:true,stationName:'',staffName:'',phone:'',address:'',usageInfo:'',facilityStatus:'INSPECTED',lastInspectionDate:'',inspectionDate:'',nextInspectionDueDate:'',stationOrder:1})
const pageCount=computed(()=>Math.max(1,Math.ceil(totalCount.value/size)))
async function load(){error.value='';try{if(editing.value){const response=await adminLine7Api.get(type.value,id.value);Object.assign(record,response?.data??response)}else if(!creating.value){const response=await adminLine7Api.list(type.value,{page:page.value,size,searchType:field.value,keyword:keyword.value});items.value=response.items;totalCount.value=response.totalCount}}catch(e){error.value=apiError(e)}}
watch(()=>route.fullPath,()=>{Object.keys(record).forEach(key=>{record[key]=({active:true,direction:'UP',status:'RUNNING',facilityStatus:'INSPECTED',stationOrder:1})[key]??''});load()},{immediate:true})
function search(){page.value=1;load()}
async function save(){saving.value=true;error.value='';const body={...record};if(!isTrain.value&&body.facilityStatus!=='IN_INSPECTION')body.inspectionDate=null;if(!isTrain.value&&!body.nextInspectionDueDate)delete body.nextInspectionDueDate;if(!isTrain.value){body.lastInspectionDate=body.lastInspectionDate||null;body.inspectionDate=body.inspectionDate||null}if(isTrain.value)body.lineId=Number(body.lineId);try{if(editing.value)await adminLine7Api.update(type.value,id.value,body);else await adminLine7Api.create(type.value,body);router.push(`/admin/line7/${type.value}`)}catch(e){error.value=apiError(e)}finally{saving.value=false}}
async function toggle(item){try{await adminLine7Api.setActive(type.value,item.trainId??item.stationId,!item.active);load()}catch(e){error.value=apiError(e)}}
function open(item){router.push(`/admin/line7/${type.value}/${item.trainId??item.stationId}`)}
function statusLabel(value){return ({RUNNING:'운행 중',MAINTENANCE:'점검 중',BROKEN:'고장',INSPECTED:'점검 완료',NEEDS_INSPECTION:'점검 필요',IN_INSPECTION:'점검 중'})[value]||value}
</script>
<template>
  <div class="line7-admin-shell"><PageHeader/><main><h1>7호선 {{ title }} 관리</h1><p v-if="error" role="alert">{{ error }}</p>
    <template v-if="!creating && !editing"><form @submit.prevent="search" class="search"><select v-model="field" aria-label="검색 기준"><option :value="isTrain?'trainNo':'stationName'">{{ isTrain?'열차번호':'역 이름' }}</option></select><input v-model="keyword" placeholder="검색어"/><button>검색</button><RouterLink :to="`/admin/line7/${type}/new`">등록</RouterLink></form>
      <table><thead><tr><th>{{ isTrain?'열차 번호':'역 이름' }}</th><th>{{ isTrain?'운행 상태':'시설 상태' }}</th><th>{{ isTrain?'운행 구간':'점검일' }}</th><th>활성 상태</th><th>관리</th></tr></thead><tbody><tr v-for="item in items" :key="item.trainId??item.stationId" :class="{inactive:!item.active}"><td>{{ item.trainNo||item.stationName }}</td><td>{{ statusLabel(isTrain?item.status:item.facilityStatus) }}</td><td>{{ isTrain?item.section:(item.inspectionDate||item.lastInspectionDate) }}</td><td>{{ item.active?'활성':'비활성' }}</td><td><button type="button" @click="open(item)">상세·수정</button><button type="button" @click="toggle(item)">{{ item.active?'비활성화':'활성화' }}</button></td></tr><tr v-if="!items.length"><td colspan="5">표시할 항목이 없습니다.</td></tr></tbody></table>
      <nav class="pages"><button :disabled="page===1" @click="page=1;load()">«</button><button :disabled="page===1" @click="page--;load()">‹</button><span>{{ page }} / {{ pageCount }}</span><button :disabled="page===pageCount" @click="page++;load()">›</button><button :disabled="page===pageCount" @click="page=pageCount;load()">»</button></nav>
    </template>
    <form v-else class="editor" @submit.prevent="save"><h2>{{ editing?'상세·수정':'등록' }}</h2>
      <template v-if="isTrain"><label>노선 ID<input v-model="record.lineId" required/></label><label>열차 번호<input v-model="record.trainNo" required/></label><label>운행 구간<input v-model="record.section" required/></label><label>기관사<input v-model="record.driver" required/></label><label>방향<select v-model="record.direction"><option value="UP">상행</option><option value="DOWN">하행</option></select></label><label>운행 상태<select v-model="record.status"><option value="RUNNING">운행 중</option><option value="MAINTENANCE">점검 중</option><option value="BROKEN">고장</option></select></label></template>
      <template v-else><label>역 이름<input v-model="record.stationName" required/></label><label>역무원<input v-model="record.staffName" required/></label><label>전화번호<input v-model="record.phone"/></label><label>주소<input v-model="record.address"/></label><label>이용 안내<textarea v-model="record.usageInfo"/></label><label>역 순서<input v-model.number="record.stationOrder" type="number" required/></label><label>시설 상태<select v-model="record.facilityStatus"><option value="INSPECTED">점검 완료</option><option value="NEEDS_INSPECTION">점검 필요</option><option value="IN_INSPECTION">점검 중</option></select></label><label>최근 점검일<input v-model="record.lastInspectionDate" type="date"/></label><label v-if="record.facilityStatus==='IN_INSPECTION'">점검일<input v-model="record.inspectionDate" type="date"/></label><label>다음 점검 예정일<input v-model="record.nextInspectionDueDate" type="date"/></label></template>
      <label class="active"><input v-model="record.active" type="checkbox"/> 활성</label><p v-if="error" role="alert">{{ error }}</p><div><RouterLink :to="`/admin/line7/${type}`">취소</RouterLink><button :disabled="saving">{{ saving?'저장 중...':editing?'저장':'등록' }}</button><button v-if="editing" type="button" @click="toggle(record)">{{ record.active?'비활성화':'활성화' }}</button></div>
    </form>
  </main><PageFooter/></div>
</template>
<style scoped>.line7-admin-shell {
  display: flex;
  flex-direction: column;
  min-height: 100svh;
  width: min(1440px,100%);
  margin: auto;
  padding: 20px 34px;
  background: var(--page-background);
}
main {
  flex: 1;
}
h1 {
  color: #155369;
}
.search,.editor {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
  padding: 20px;
  background: #f0f5f7;
  border-radius: 12px;
  margin: 20px 0;
}
.editor {
  display: grid;
  grid-template-columns: repeat(2,minmax(0,1fr));
}
.editor h2,.editor>div,.editor>p {
  grid-column: 1/-1;
}
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: #155369;
}
input,select,textarea {
  padding: 10px;
  border: 2px solid #c6dadd;
  border-radius: 8px;
  background: #fff;
  font: inherit;
}
button,.search>a,.editor a {
  padding: 9px 14px;
  border: 0;
  border-radius: 8px;
  background: #457384;
  color: white;
  text-decoration: none;
  cursor: pointer;
}
table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}
th,td {
  padding: 12px;
  border: 1px solid #d5e1e4;
  text-align: left;
}
.inactive {
  color: #999;
  background: #eee;
}
.pages {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin: 20px;
}
.active {
  flex-direction: row;
  align-items: center;
}
@media (max-width:700px) {
  .editor {
    grid-template-columns: 1fr;
  }
  .line7-admin-shell {
    padding: 16px;
  }
}
</style>
