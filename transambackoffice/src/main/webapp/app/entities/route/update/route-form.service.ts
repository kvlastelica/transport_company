import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRoute, NewRoute } from '../route.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRoute for edit and NewRouteFormGroupInput for create.
 */
type RouteFormGroupInput = IRoute | PartialWithRequiredKeyOf<NewRoute>;

type RouteFormDefaults = Pick<NewRoute, 'id' | 'employees' | 'vehicles'>;

type RouteFormGroupContent = {
  id: FormControl<IRoute['id'] | NewRoute['id']>;
  code: FormControl<IRoute['code']>;
  description: FormControl<IRoute['description']>;
  start: FormControl<IRoute['start']>;
  destination: FormControl<IRoute['destination']>;
  passengerCapacity: FormControl<IRoute['passengerCapacity']>;
  parcelTotalWeight: FormControl<IRoute['parcelTotalWeight']>;
  employees: FormControl<IRoute['employees']>;
  vehicles: FormControl<IRoute['vehicles']>;
};

export type RouteFormGroup = FormGroup<RouteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RouteFormService {
  createRouteFormGroup(route: RouteFormGroupInput = { id: null }): RouteFormGroup {
    const routeRawValue = {
      ...this.getFormDefaults(),
      ...route,
    };
    return new FormGroup<RouteFormGroupContent>({
      id: new FormControl(
        { value: routeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(routeRawValue.code, {
        validators: [Validators.required],
      }),
      description: new FormControl(routeRawValue.description, {
        validators: [Validators.required],
      }),
      start: new FormControl(routeRawValue.start, {
        validators: [Validators.required],
      }),
      destination: new FormControl(routeRawValue.destination, {
        validators: [Validators.required],
      }),
      passengerCapacity: new FormControl(routeRawValue.passengerCapacity, {
        validators: [Validators.required],
      }),
      parcelTotalWeight: new FormControl(routeRawValue.parcelTotalWeight, {
        validators: [Validators.required],
      }),
      employees: new FormControl(routeRawValue.employees ?? []),
      vehicles: new FormControl(routeRawValue.vehicles ?? []),
    });
  }

  getRoute(form: RouteFormGroup): IRoute | NewRoute {
    return form.getRawValue() as IRoute | NewRoute;
  }

  resetForm(form: RouteFormGroup, route: RouteFormGroupInput): void {
    const routeRawValue = { ...this.getFormDefaults(), ...route };
    form.reset(
      {
        ...routeRawValue,
        id: { value: routeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RouteFormDefaults {
    return {
      id: null,
      employees: [],
      vehicles: [],
    };
  }
}
