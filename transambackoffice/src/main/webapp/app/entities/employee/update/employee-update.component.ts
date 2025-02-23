import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';
import { IRoute } from 'app/entities/route/route.model';
import { RouteService } from 'app/entities/route/service/route.service';
import { EmployeeService } from '../service/employee.service';
import { IEmployee } from '../employee.model';
import { EmployeeFormGroup, EmployeeFormService } from './employee-form.service';

@Component({
  selector: 'jhi-employee-update',
  templateUrl: './employee-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EmployeeUpdateComponent implements OnInit {
  isSaving = false;
  employee: IEmployee | null = null;

  usersSharedCollection: IUser[] = [];
  departmentsSharedCollection: IDepartment[] = [];
  routesSharedCollection: IRoute[] = [];

  protected employeeService = inject(EmployeeService);
  protected employeeFormService = inject(EmployeeFormService);
  protected userService = inject(UserService);
  protected departmentService = inject(DepartmentService);
  protected routeService = inject(RouteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EmployeeFormGroup = this.employeeFormService.createEmployeeFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareDepartment = (o1: IDepartment | null, o2: IDepartment | null): boolean => this.departmentService.compareDepartment(o1, o2);

  compareRoute = (o1: IRoute | null, o2: IRoute | null): boolean => this.routeService.compareRoute(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ employee }) => {
      this.employee = employee;
      if (employee) {
        this.updateForm(employee);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const employee = this.employeeFormService.getEmployee(this.editForm);
    if (employee.id !== null) {
      this.subscribeToSaveResponse(this.employeeService.update(employee));
    } else {
      this.subscribeToSaveResponse(this.employeeService.create(employee));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmployee>>): void {
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

  protected updateForm(employee: IEmployee): void {
    this.employee = employee;
    this.employeeFormService.resetForm(this.editForm, employee);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, employee.user);
    this.departmentsSharedCollection = this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(
      this.departmentsSharedCollection,
      employee.department,
    );
    this.routesSharedCollection = this.routeService.addRouteToCollectionIfMissing<IRoute>(
      this.routesSharedCollection,
      ...(employee.routes ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.employee?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.departmentService
      .query()
      .pipe(map((res: HttpResponse<IDepartment[]>) => res.body ?? []))
      .pipe(
        map((departments: IDepartment[]) =>
          this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(departments, this.employee?.department),
        ),
      )
      .subscribe((departments: IDepartment[]) => (this.departmentsSharedCollection = departments));

    this.routeService
      .query()
      .pipe(map((res: HttpResponse<IRoute[]>) => res.body ?? []))
      .pipe(map((routes: IRoute[]) => this.routeService.addRouteToCollectionIfMissing<IRoute>(routes, ...(this.employee?.routes ?? []))))
      .subscribe((routes: IRoute[]) => (this.routesSharedCollection = routes));
  }
}
