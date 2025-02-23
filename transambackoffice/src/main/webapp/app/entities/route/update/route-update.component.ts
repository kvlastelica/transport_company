import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';
import { IVehicle } from 'app/entities/vehicle/vehicle.model';
import { VehicleService } from 'app/entities/vehicle/service/vehicle.service';
import { RouteService } from '../service/route.service';
import { IRoute } from '../route.model';
import { RouteFormGroup, RouteFormService } from './route-form.service';

@Component({
  selector: 'jhi-route-update',
  templateUrl: './route-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RouteUpdateComponent implements OnInit {
  isSaving = false;
  route: IRoute | null = null;

  employeesSharedCollection: IEmployee[] = [];
  vehiclesSharedCollection: IVehicle[] = [];

  protected routeService = inject(RouteService);
  protected routeFormService = inject(RouteFormService);
  protected employeeService = inject(EmployeeService);
  protected vehicleService = inject(VehicleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RouteFormGroup = this.routeFormService.createRouteFormGroup();

  compareEmployee = (o1: IEmployee | null, o2: IEmployee | null): boolean => this.employeeService.compareEmployee(o1, o2);

  compareVehicle = (o1: IVehicle | null, o2: IVehicle | null): boolean => this.vehicleService.compareVehicle(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ route }) => {
      this.route = route;
      if (route) {
        this.updateForm(route);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const route = this.routeFormService.getRoute(this.editForm);
    if (route.id !== null) {
      this.subscribeToSaveResponse(this.routeService.update(route));
    } else {
      this.subscribeToSaveResponse(this.routeService.create(route));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRoute>>): void {
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

  protected updateForm(route: IRoute): void {
    this.route = route;
    this.routeFormService.resetForm(this.editForm, route);

    this.employeesSharedCollection = this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(
      this.employeesSharedCollection,
      ...(route.employees ?? []),
    );
    this.vehiclesSharedCollection = this.vehicleService.addVehicleToCollectionIfMissing<IVehicle>(
      this.vehiclesSharedCollection,
      ...(route.vehicles ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.employeeService
      .query()
      .pipe(map((res: HttpResponse<IEmployee[]>) => res.body ?? []))
      .pipe(
        map((employees: IEmployee[]) =>
          this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(employees, ...(this.route?.employees ?? [])),
        ),
      )
      .subscribe((employees: IEmployee[]) => (this.employeesSharedCollection = employees));

    this.vehicleService
      .query()
      .pipe(map((res: HttpResponse<IVehicle[]>) => res.body ?? []))
      .pipe(
        map((vehicles: IVehicle[]) =>
          this.vehicleService.addVehicleToCollectionIfMissing<IVehicle>(vehicles, ...(this.route?.vehicles ?? [])),
        ),
      )
      .subscribe((vehicles: IVehicle[]) => (this.vehiclesSharedCollection = vehicles));
  }
}
