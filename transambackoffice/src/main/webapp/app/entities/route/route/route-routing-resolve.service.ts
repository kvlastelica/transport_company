import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRoute } from '../route.model';
import { RouteService } from '../service/route.service';

const routeResolve = (route: ActivatedRouteSnapshot): Observable<null | IRoute> => {
  const id = route.params.id;
  if (id) {
    return inject(RouteService)
      .find(id)
      .pipe(
        mergeMap((route: HttpResponse<IRoute>) => {
          if (route.body) {
            return of(route.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default routeResolve;
