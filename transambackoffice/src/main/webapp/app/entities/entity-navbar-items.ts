import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Department',
    route: '/department',
    translationKey: 'global.menu.entities.department',
  },
  {
    name: 'Employee',
    route: '/employee',
    translationKey: 'global.menu.entities.employee',
  },
  {
    name: 'Vehicle',
    route: '/vehicle',
    translationKey: 'global.menu.entities.vehicle',
  },
  {
    name: 'Route',
    route: '/route',
    translationKey: 'global.menu.entities.route',
  },
  {
    name: 'RouteSchedule',
    route: '/route-schedule',
    translationKey: 'global.menu.entities.routeSchedule',
  },
];
