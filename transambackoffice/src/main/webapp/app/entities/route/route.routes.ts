import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RouteResolve from './route/route-routing-resolve.service';

const routeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/route.component').then(m => m.RouteComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/route-detail.component').then(m => m.RouteDetailComponent),
    resolve: {
      route: RouteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/route-update.component').then(m => m.RouteUpdateComponent),
    resolve: {
      route: RouteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/route-update.component').then(m => m.RouteUpdateComponent),
    resolve: {
      route: RouteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default routeRoute;
