import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRouteSchedule, NewRouteSchedule } from '../route-schedule.model';

export type PartialUpdateRouteSchedule = Partial<IRouteSchedule> & Pick<IRouteSchedule, 'id'>;

type RestOf<T extends IRouteSchedule | NewRouteSchedule> = Omit<T, 'departure' | 'arrival'> & {
  departure?: string | null;
  arrival?: string | null;
};

export type RestRouteSchedule = RestOf<IRouteSchedule>;

export type NewRestRouteSchedule = RestOf<NewRouteSchedule>;

export type PartialUpdateRestRouteSchedule = RestOf<PartialUpdateRouteSchedule>;

export type EntityResponseType = HttpResponse<IRouteSchedule>;
export type EntityArrayResponseType = HttpResponse<IRouteSchedule[]>;

@Injectable({ providedIn: 'root' })
export class RouteScheduleService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/route-schedules');

  create(routeSchedule: NewRouteSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(routeSchedule);
    return this.http
      .post<RestRouteSchedule>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(routeSchedule: IRouteSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(routeSchedule);
    return this.http
      .put<RestRouteSchedule>(`${this.resourceUrl}/${this.getRouteScheduleIdentifier(routeSchedule)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(routeSchedule: PartialUpdateRouteSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(routeSchedule);
    return this.http
      .patch<RestRouteSchedule>(`${this.resourceUrl}/${this.getRouteScheduleIdentifier(routeSchedule)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRouteSchedule>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRouteSchedule[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRouteScheduleIdentifier(routeSchedule: Pick<IRouteSchedule, 'id'>): number {
    return routeSchedule.id;
  }

  compareRouteSchedule(o1: Pick<IRouteSchedule, 'id'> | null, o2: Pick<IRouteSchedule, 'id'> | null): boolean {
    return o1 && o2 ? this.getRouteScheduleIdentifier(o1) === this.getRouteScheduleIdentifier(o2) : o1 === o2;
  }

  addRouteScheduleToCollectionIfMissing<Type extends Pick<IRouteSchedule, 'id'>>(
    routeScheduleCollection: Type[],
    ...routeSchedulesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const routeSchedules: Type[] = routeSchedulesToCheck.filter(isPresent);
    if (routeSchedules.length > 0) {
      const routeScheduleCollectionIdentifiers = routeScheduleCollection.map(routeScheduleItem =>
        this.getRouteScheduleIdentifier(routeScheduleItem),
      );
      const routeSchedulesToAdd = routeSchedules.filter(routeScheduleItem => {
        const routeScheduleIdentifier = this.getRouteScheduleIdentifier(routeScheduleItem);
        if (routeScheduleCollectionIdentifiers.includes(routeScheduleIdentifier)) {
          return false;
        }
        routeScheduleCollectionIdentifiers.push(routeScheduleIdentifier);
        return true;
      });
      return [...routeSchedulesToAdd, ...routeScheduleCollection];
    }
    return routeScheduleCollection;
  }

  protected convertDateFromClient<T extends IRouteSchedule | NewRouteSchedule | PartialUpdateRouteSchedule>(routeSchedule: T): RestOf<T> {
    return {
      ...routeSchedule,
      departure: routeSchedule.departure?.toJSON() ?? null,
      arrival: routeSchedule.arrival?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRouteSchedule: RestRouteSchedule): IRouteSchedule {
    return {
      ...restRouteSchedule,
      departure: restRouteSchedule.departure ? dayjs(restRouteSchedule.departure) : undefined,
      arrival: restRouteSchedule.arrival ? dayjs(restRouteSchedule.arrival) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRouteSchedule>): HttpResponse<IRouteSchedule> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRouteSchedule[]>): HttpResponse<IRouteSchedule[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
