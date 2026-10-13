import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import SignUp from '../views/SignUp.vue'
import Main from '../views/Main.vue'
import Resource from '../views/Resource.vue'
import AdminResource from '../views/AdminResource.vue'
import AdminMember from '../views/AdminMember.vue'
import QnaCreate from '../views/QnaCreate.vue'
import MyPage from '../views/MyPage.vue'
import ContentPage from '../views/ContentPage.vue'
import Terms from '../views/Terms.vue'
import ErrorPage from '../views/Error.vue'
import { requireMember } from '../services/accessControl'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: Main },
    { path: '/notices/:id?', component: ContentPage, meta: { title: '공지사항', kind: 'board' } },
    { path: '/admin/resources/new', name: 'admin-resource-new', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/admin/resources/:resourceId/edit', name: 'admin-resource-edit', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/admin/resources/:resourceId?', name: 'admin-resources', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/resources/:resourceId?', component: Resource, meta: { title: '자료실', requiresMember: true } },
    { path: '/qna/create', component: QnaCreate, meta: { requiresMember: true } },
    { path: '/qna/:id?', component: ContentPage, meta: { title: 'Q&A', kind: 'board', requiresMember: true } },
    { path: '/rail-info', component: ContentPage, meta: { title: '철도정보', kind: 'rail' } },
    { path: '/mypage', component: MyPage, meta: { requiresMember: true } },
    { path: '/mypage/delete', component: MyPage, meta: { requiresMember: true } },
    { path: '/members', redirect: '/admin/members' },
    { path: '/admin/members/:memberId?', component: AdminMember, meta: { requiresAdmin: true } },
    { path: '/login', name: 'login', component: Login },
    { path: '/signup', name: 'signup', component: SignUp },
    { path: '/terms', name: 'terms', component: Terms },
    { path: '/error/:code(401|403|404|500)', name: 'error', component: ErrorPage },
    { path: '/:pathMatch(.*)*', redirect: '/error/404' },
  ],
})



router.beforeEach((to, from) => {
  if (!requireMember(to)) return from.matched.length ? false : '/'
})
export default router