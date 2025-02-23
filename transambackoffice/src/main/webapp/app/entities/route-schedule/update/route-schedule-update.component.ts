import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IRoute } from 'app/entities/route/route.model';
import { RouteService } from 'app/entities/route/service/route.service';
import { IRouteSchedule } from '../route-schedule.model';
import { RouteScheduleService } from '../service/route-schedule.service';
import { RouteScheduleFormGroup, RouteScheduleFormService } from './route-schedule-form.service';

@Component({
  selector: 'jhi-route-schedule-update',
  templateUrl: './route-schedule-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RouteScheduleUpdateComponent implements OnInit {
  isSaving = false;
  routeSchedule: IRouteSchedule | null = null;

  routesSharedCollection: IRoute[] = [];

  protected routeScheduleService = inject(RouteScheduleService);
  protected routeScheduleFormService = inject(RouteScheduleFormService);
  protected routeService = inject(RouteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RouteScheduleFormGroup = this.routeScheduleFormService.createRouteScheduleFormGroup();

  compareRoute = (o1: IRoute | null, o2: IRoute | null): boolean => this.routeService.compareRoute(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ routeSchedule }) => {
      this.routeSchedule = routeSchedule;
      if (routeSchedule) {
        this.updateForm(routeSchedule);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const routeSchedule = this.routeScheduleFormService.getRouteSchedule(this.editForm);
    if (routeSchedule.id !== null) {
      this.subscribeToSaveResponse(this.routeScheduleService.update(routeSchedule));
    } else {
      this.subscribeToSaveResponse(this.routeScheduleService.create(routeSchedule));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRouteSchedule>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(routeSchedule: IRouteSchedule): void {
    this.routeSchedule = routeSchedule;
    this.routeScheduleFormService.resetForm(this.editForm, routeSchedule);

    this.routesSharedCollection = this.routeService.addRouteToCollectionIfMissing<IRoute>(this.routesSharedCollection, routeSchedule.route);
  }

  protected loadRelationshipsOptions(): void {
    this.routeService
      .query()
      .pipe(map((res: HttpResponse<IRoute[]>) => res.body ?? []))
      .pipe(map((routes: IRoute[]) => this.routeService.addRouteToCollectionIfMissing<IRoute>(routes, this.routeSchedule?.route)))
      .subscribe((routes: IRoute[]) => (this.routesSharedCollection = routes));
  }
}
