import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRoute, NewRoute } from '../route.model';

export type PartialUpdateRoute = Partial<IRoute> & Pick<IRoute, 'id'>;

export type EntityResponseType = HttpResponse<IRoute>;
export type EntityArrayResponseType = HttpResponse<IRoute[]>;

@Injectable({ providedIn: 'root' })
export class RouteService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/routes');

  create(route: NewRoute): Observable<EntityResponseType> {
    return this.http.post<IRoute>(this.resourceUrl, route, { observe: 'response' });
  }

  update(route: IRoute): Observable<EntityResponseType> {
    return this.http.put<IRoute>(`${this.resourceUrl}/${this.getRouteIdentifier(route)}`, route, { observe: 'response' });
  }

  partialUpdate(route: PartialUpdateRoute): Observable<EntityResponseType> {
    return this.http.patch<IRoute>(`${this.resourceUrl}/${this.getRouteIdentifier(route)}`, route, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRoute>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRoute[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRouteIdentifier(route: Pick<IRoute, 'id'>): number {
    return route.id;
  }

  compareRoute(o1: Pick<IRoute, 'id'> | null, o2: Pick<IRoute, 'id'> | null): boolean {
    return o1 && o2 ? this.getRouteIdentifier(o1) === this.getRouteIdentifier(o2) : o1 === o2;
  }

  addRouteToCollectionIfMissing<Type extends Pick<IRoute, 'id'>>(
    routeCollection: Type[],
    ...routesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const routes: Type[] = routesToCheck.filter(isPresent);
    if (routes.length > 0) {
      const routeCollectionIdentifiers = routeCollection.map(routeItem => this.getRouteIdentifier(routeItem));
      const routesToAdd = routes.filter(routeItem => {
        const routeIdentifier = this.getRouteIdentifier(routeItem);
        if (routeCollectionIdentifiers.includes(routeIdentifier)) {
          return false;
        }
        routeCollectionIdentifiers.push(routeIdentifier);
        return true;
      });
      return [...routesToAdd, ...routeCollection];
    }
    return routeCollection;
  }
}
