import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RouteScheduleResolve from './route/route-schedule-routing-resolve.service';

const routeScheduleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/route-schedule.component').then(m => m.RouteScheduleComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/route-schedule-detail.component').then(m => m.RouteScheduleDetailComponent),
    resolve: {
      routeSchedule: RouteScheduleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/route-schedule-update.component').then(m => m.RouteScheduleUpdateComponent),
    resolve: {
      routeSchedule: RouteScheduleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/route-schedule-update.component').then(m => m.RouteScheduleUpdateComponent),
    resolve: {
      routeSchedule: RouteScheduleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default routeScheduleRoute;
