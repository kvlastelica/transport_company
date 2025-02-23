import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRouteSchedule } from '../route-schedule.model';
import { RouteScheduleService } from '../service/route-schedule.service';

const routeScheduleResolve = (route: ActivatedRouteSnapshot): Observable<null | IRouteSchedule> => {
  const id = route.params.id;
  if (id) {
    return inject(RouteScheduleService)
      .find(id)
      .pipe(
        mergeMap((routeSchedule: HttpResponse<IRouteSchedule>) => {
          if (routeSchedule.body) {
            return of(routeSchedule.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default routeScheduleResolve;
