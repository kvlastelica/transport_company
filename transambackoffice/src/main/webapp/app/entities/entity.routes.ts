import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'transambackofficeApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'department',
    data: { pageTitle: 'transambackofficeApp.department.home.title' },
    loadChildren: () => import('./department/department.routes'),
  },
  {
    path: 'employee',
    data: { pageTitle: 'transambackofficeApp.employee.home.title' },
    loadChildren: () => import('./employee/employee.routes'),
  },
  {
    path: 'vehicle',
    data: { pageTitle: 'transambackofficeApp.vehicle.home.title' },
    loadChildren: () => import('./vehicle/vehicle.routes'),
  },
  {
    path: 'route',
    data: { pageTitle: 'transambackofficeApp.route.home.title' },
    loadChildren: () => import('./route/route.routes'),
  },
  {
    path: 'route-schedule',
    data: { pageTitle: 'transambackofficeApp.routeSchedule.home.title' },
    loadChildren: () => import('./route-schedule/route-schedule.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
